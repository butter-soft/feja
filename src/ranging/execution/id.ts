import { Schema } from "effect";

export const RangingExecutionIdSchema = Schema.String.check(Schema.isUUID());
