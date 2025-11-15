"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const img = document.getElementById('frame');
const stats = document.getElementById('stats');
// Paste base64 data of a saved processed frame (from Android)
const base64 = "data:image/png;base64,PUT_BASE64_HERE";
img.src = base64;
stats.innerText = `FPS: N/A  Resolution: 640x480`;
//# sourceMappingURL=app.js.map