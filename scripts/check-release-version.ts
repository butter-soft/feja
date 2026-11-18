import { readFileSync } from "node:fs";

const { version } = JSON.parse(readFileSync(new URL("../package.json", import.meta.url), "utf8"));
const expected = `v${version}`;
const actual = process.env.RELEASE_TAG;

if (actual !== expected) {
  throw new Error(`Release tag ${actual} does not match ${expected}.`);
}

console.log(`Release version verified: ${version}`);
