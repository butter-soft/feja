import type { NativeCommandFailure } from "../native/command-result";
import type { NativePayloadIssue } from "../native/payload-issues";

export class BeaconCommandError extends Error {
  readonly failure: NativeCommandFailure;

  constructor(failure: NativeCommandFailure) {
    super(failure.message);
    this.name = "BeaconCommandError";
    this.failure = failure;
  }
}

export class BeaconResponseError extends Error {
  readonly issues: readonly NativePayloadIssue[];

  constructor(issues: readonly NativePayloadIssue[]) {
    const descriptions = issues.map(
      (issue) => `${["$", ...issue.path].map(String).join(".")}: ${issue.message}`,
    );

    super(["Invalid native response.", ...descriptions].join("\n"));
    this.name = "BeaconResponseError";
    this.issues = issues;
  }
}
