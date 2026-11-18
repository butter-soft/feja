import { NativeModule, requireNativeModule } from "expo";
import type { BeaconTargetInput } from "../beacon/target";

type NativeEvents = {
  onRangingStarted: (payload: unknown) => void;
  onRangingReceived: (payload: unknown) => void;
  onRangingTerminated: (payload: unknown) => void;
  onRangingMeasurementParseError: (payload: unknown) => void;
};

declare class FejaModule extends NativeModule<NativeEvents> {
  start(target: BeaconTargetInput): Promise<unknown>;
  stop(target: BeaconTargetInput): Promise<unknown>;
  getSnapshot(): Promise<unknown>;
}

export default requireNativeModule<FejaModule>("Feja");
