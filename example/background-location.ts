import * as Location from "expo-location";
import { LOCATION_TASK_NAME } from "./location-task";

export const prepareBackgroundPermission = async () => {
  const permission = await Location.requestBackgroundPermissionsAsync();

  console.log("Background permission:", permission);

  if (!permission.granted) {
    throw new Error("Always location permission is required.");
  }
};

export const startBackgroundLocation = async () => {
  await Location.startLocationUpdatesAsync(LOCATION_TASK_NAME, {
    accuracy: Location.Accuracy.Lowest,
    distanceInterval: 3000,
    pausesUpdatesAutomatically: false,
    activityType: Location.ActivityType.Other,
    showsBackgroundLocationIndicator: true,
    deferredUpdatesDistance: 0,
    deferredUpdatesInterval: 0,
  });
};

export const stopBackgroundLocation = async () => {
  if (await Location.hasStartedLocationUpdatesAsync(LOCATION_TASK_NAME)) {
    await Location.stopLocationUpdatesAsync(LOCATION_TASK_NAME);
  }
};
