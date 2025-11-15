const img = document.getElementById('frame') as HTMLImageElement;
const stats = document.getElementById('stats') as HTMLDivElement;

// Paste base64 data of a saved processed frame (from Android)
const base64 = "data:image/png;base64,PUT_BASE64_HERE";
img.src = base64;
stats.innerText = `FPS: N/A  Resolution: 640x480`;
