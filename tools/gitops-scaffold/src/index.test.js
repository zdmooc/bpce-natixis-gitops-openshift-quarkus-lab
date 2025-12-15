import test from "node:test";
import assert from "node:assert/strict";
import fs from "node:fs";
import path from "node:path";
import { execFileSync } from "node:child_process";

test("cli generates directories", () => {
  const tmp = fs.mkdtempSync("gitops-scaffold-");
  execFileSync("node", ["src/index.js", "--name", "xapp", "--out", tmp], { stdio: "inherit" });

  assert.ok(fs.existsSync(path.join(tmp, "base", "kustomization.yaml")));
  assert.ok(fs.existsSync(path.join(tmp, "overlays", "dev", "kustomization.yaml")));
});
