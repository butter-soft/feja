import { SchemaIssue } from "effect";
import type { Schema } from "effect";

export type NativePayloadIssue = {
  readonly path: readonly PropertyKey[];
  readonly message: string;
};

export const makeNativePayloadIssues = (error: Schema.SchemaError): readonly NativePayloadIssue[] =>
  SchemaIssue.makeFormatterStandardSchemaV1()(error.issue).issues.map((issue) => ({
    path: (issue.path ?? []).map((segment) =>
      typeof segment === "object" ? segment.key : segment,
    ),
    message: issue.message,
  }));
