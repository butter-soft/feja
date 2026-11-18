import { PermissionsAndroid, Platform } from "react-native";

export const prepareBluetoothPermissions = async () => {
  if (Platform.OS !== "android" || Platform.Version < 31) {
    return;
  }

  const results = await PermissionsAndroid.requestMultiple([
    PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN,
    PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT,
  ]);

  if (
    Object.values(results).some((status) => status === PermissionsAndroid.RESULTS.NEVER_ASK_AGAIN)
  ) {
    throw new Error("Enable Nearby devices permission in Settings.");
  }

  if (!Object.values(results).every((status) => status === PermissionsAndroid.RESULTS.GRANTED)) {
    throw new Error("Bluetooth permissions are required.");
  }
};
