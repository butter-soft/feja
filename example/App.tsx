import { Button, Platform, ScrollView, Text } from "react-native";
import { SafeAreaProvider, SafeAreaView } from "react-native-safe-area-context";
import { type BeaconTargetInput, getRangingSnapshot, useRanging } from "feja";
import { useState } from "react";
import { requestForegroundPermissionsAsync } from "expo-location";
import {
  prepareBackgroundPermission,
  startBackgroundLocation,
  stopBackgroundLocation,
} from "./background-location";
import { prepareBluetoothPermissions } from "./bluetooth-permissions";

const target: BeaconTargetInput = {
  uuid: "706E7568-5F70-656F-706C-655F74610032",
};

const RangingExample = () => {
  const [receivedCount, setReceivedCount] = useState(0);

  const {
    snapshot: rangingSnapshot,
    start: startNativeRanging,
    stop: stopNativeRanging,
  } = useRanging({
    onStarted: (event) => {
      console.log("Ranging started:", event);
    },
    onReceived: (event) => {
      console.log("Ranging received:", event);
      setReceivedCount((count) => count + 1);
    },
    onTerminated: (event) => {
      console.log("Ranging terminated:", event);
    },
    onMeasurementParseError: (event) => {
      console.error("Measurement parse error:", event);
    },
  });

  const preparePermissions = async () => {
    const permission = await requestForegroundPermissionsAsync();

    console.log("Location permission:", permission);

    if (!permission.granted) {
      throw new Error("Location permission is required.");
    }

    if (Platform.OS === "ios" && permission.ios?.accuracy !== "full") {
      throw new Error("Precise location is required.");
    }

    await prepareBluetoothPermissions();
  };

  const start = async () => {
    if ((await getRangingSnapshot()).state.status === "running") {
      throw new Error("Ranging is already running.");
    }

    await preparePermissions();

    if (Platform.OS === "ios") {
      await prepareBackgroundPermission();
    }

    await startNativeRanging(target);

    try {
      if (Platform.OS === "ios") {
        await startBackgroundLocation();
      }
    } catch (error) {
      try {
        await stop();
      } catch (cleanupError) {
        throw new AggregateError([error, cleanupError], "Location startup and cleanup failed.");
      }

      throw error;
    }
  };

  const stop = async () => {
    const results = await Promise.allSettled([
      stopNativeRanging(target),
      ...(Platform.OS === "ios" ? [stopBackgroundLocation()] : []),
    ]);

    const errors = results.flatMap((result) =>
      result.status === "rejected" ? [result.reason] : [],
    );

    if (errors.length > 0) {
      throw new AggregateError(errors, "Failed to stop all services.");
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>Module API Example</Text>
      <Text>Ranging: {rangingSnapshot?.state.status ?? "loading"}</Text>
      <Text>Revision: {rangingSnapshot?.revision ?? "loading"}</Text>
      <Text>Received: {receivedCount}</Text>
      <Button
        title="Ranging 시작"
        onPress={() => {
          void start().catch(console.error);
        }}
      />
      <Button
        title="같은 대상 다시 시작"
        onPress={() => {
          void startNativeRanging(target).catch(console.error);
        }}
      />
      <Button
        title="Ranging 중단"
        onPress={() => {
          void stop().catch(console.error);
        }}
      />
      <Button
        title="Snapshot 조회"
        onPress={() => {
          void getRangingSnapshot()
            .then((snapshot) => {
              console.log("Ranging snapshot:", snapshot);
            })
            .catch(console.error);
        }}
      />
    </ScrollView>
  );
};

export default function App() {
  const [visible, setVisible] = useState(true);

  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.container}>
        <Button
          title={visible ? "관찰 컴포넌트 숨기기" : "관찰 컴포넌트 표시"}
          onPress={() => {
            setVisible((current) => !current);
          }}
        />
        {visible && <RangingExample />}
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

const styles = {
  header: { fontSize: 30, margin: 20 },
  container: { flex: 1, backgroundColor: "#eee" },
  view: { flex: 1, height: 200 },
};
