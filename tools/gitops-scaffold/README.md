# gitops-scaffold (Node.js)

Petit CLI JavaScript pour générer une arborescence Kustomize “base + overlays”.

## Usage
```bash
cd tools/gitops-scaffold
npm ci
node src/index.js --name myapp --out ../../deploy/kustomize
```

## Tests
```bash
npm test
```
