# feja

An Expo module for building proximity-aware experiences with BLE beacons on iOS and Android.

## React usage

Use `useRanging` to observe the shared native execution and issue commands. Each callback is optional.

```tsx
import { useRanging } from 'feja';
import { Button, Text, View } from 'react-native';

const target = { uuid: 'YOUR-BEACON-UUID' };

export function BeaconExample() {
  const { snapshot, start, stop } = useRanging({
    onStarted: (event) => console.log('Started:', event),
    onReceived: (event) => console.log('Measurements:', event.measurements),
    onTerminated: (event) => console.log('Terminated:', event),
    onMeasurementParseError: (event) => console.error('Measurement parse error:', event),
  });

  return (
    <View>
      <Text>{snapshot?.state.status ?? 'loading'}</Text>
      <Button title="Start" onPress={() => void start(target).catch(console.error)} />
      <Button title="Stop" onPress={() => void stop(target).catch(console.error)} />
    </View>
  );
}
```

The app prepares platform permissions before starting. App background execution setup is also managed by the app; see the [example](./example/App.tsx) for a working configuration.

## Execution and observation

- One native ranging execution is shared by commands and observers.
- A target specifies a UUID, optionally narrowed by major, or by major and minor.
- Starting the active target preserves its execution ID. Starting a different target rejects with a target conflict.
- A new execution emits `onStarted` once. Its `executionId` identifies subsequent events from that execution.
- `snapshot` starts as `null`. The hook queries the current snapshot on mount and updates it from start and termination events.
- A snapshot contains `{ revision, state }`. The revision increases on successful new starts and execution terminations, within one native manager lifetime.
- Each hook manages its own observation. Unmounting releases its subscriptions while the native execution continues. Mounting again reads the current snapshot and receives subsequent events.
- `start` and `stop` return promises and reject on command failure. Termination events report either `stopped` or `failed`.

## Direct API

The same commands, query, and subscriptions are available individually:

- `startRanging(target)` / `stopRanging(target)`
- `getRangingSnapshot()`
- `subscribeRangingStarted(onEvent, onDecodeError)`
- `subscribeRangingReceived(onEvent, onDecodeError)`
- `subscribeRangingTerminated(onEvent, onDecodeError)`
- `subscribeRangingMeasurementParseError(onEvent, onDecodeError)`

Each subscription returns a handle with `remove()`. Its decode error callback receives payload validation issues.

## Run the example

The example has its own dependencies and uses the local package through `file:..`.

```sh
pnpm install
pnpm build
cd example
pnpm install
pnpm android
# or: pnpm ios
```

The app includes controls for starting, stopping, querying snapshots, and mounting or unmounting an observer.
