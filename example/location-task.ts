import * as TaskManager from "expo-task-manager";
import type { LocationObject } from "expo-location";

export const LOCATION_TASK_NAME = "feja-example-location";

TaskManager.defineTask<{ locations: LocationObject[] }>(
  LOCATION_TASK_NAME,
  async ({ data, error }) => {
    if (error) {
      console.error("Location task failed:", error);
      return;
    }

    console.log("Location batch:", data.locations.length);
  },
);
