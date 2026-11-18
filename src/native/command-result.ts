import { Schema } from "effect";

import { BeaconTargetSchema } from "../beacon/target";

const NativeCommandErrorCodeSchema = Schema.Literals([
  "ERR_IBEACON_INVALID_UUID",
  "ERR_IBEACON_INVALID_IDENTITY_VALUE",
  "ERR_IBEACON_MINOR_WITHOUT_MAJOR",
  "ERR_IBEACON_RANGING_UNAVAILABLE",
  "ERR_IBEACON_EXECUTION_TERMINATED",
  "ERR_IBEACON_UNSUPPORTED_AUTHORIZATION_STATUS",
  "ERR_IBEACON_UNSUPPORTED_ACCURACY_AUTHORIZATION",
  "ERR_IBEACON_AUTHORIZATION_REQUIRED",
  "ERR_IBEACON_FULL_ACCURACY_REQUIRED",
]);

const NativeLibraryFailureSchema = Schema.Struct({
  exceptionType: Schema.String,
  message: Schema.NullOr(Schema.String),
});

type NativeCleanupFailureDetails = {
  readonly startError: NativeCommandFailure;
  readonly cleanupFailure: typeof NativeLibraryFailureSchema.Type;
};

const NativeCleanupFailureDetailsSchema: Schema.Codec<NativeCleanupFailureDetails> = Schema.Struct({
  startError: Schema.suspend(() => NativeCommandFailureSchema),
  cleanupFailure: NativeLibraryFailureSchema,
});

export const NativeCommandFailureSchema = Schema.Union([
  Schema.Struct({
    code: Schema.Literal("ERR_IBEACON_TARGET_CONFLICT"),
    message: Schema.String,
    details: Schema.Struct({
      active: BeaconTargetSchema,
      requested: BeaconTargetSchema,
    }),
  }),
  Schema.Struct({
    code: NativeCommandErrorCodeSchema,
    message: Schema.String,
    details: Schema.Record(Schema.String, Schema.Never),
  }),
  Schema.Struct({
    code: Schema.Literals(["ERR_IBEACON_RANGING_START_FAILED", "ERR_IBEACON_RANGING_STOP_FAILED"]),
    message: Schema.String,
    details: NativeLibraryFailureSchema,
  }),
  Schema.Struct({
    code: Schema.Literal("ERR_IBEACON_RANGING_START_CLEANUP_FAILED"),
    message: Schema.String,
    details: NativeCleanupFailureDetailsSchema,
  }),
]);

export const NativeCommandResultSchema = Schema.Union([
  Schema.Struct({
    status: Schema.Literal("success"),
  }),
  Schema.Struct({
    status: Schema.Literal("failure"),
    error: NativeCommandFailureSchema,
  }),
]);

export const decodeNativeCommandResult = Schema.decodeUnknownResult(NativeCommandResultSchema);

export type NativeCommandFailure = typeof NativeCommandFailureSchema.Type;

export type NativeCommandResult = typeof NativeCommandResultSchema.Type;
