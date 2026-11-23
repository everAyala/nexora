// Service worker de Nexora.
// Guarda en cache la "carcasa" de la app (HTML, CSS, JS, iconos) para que abra rapido
// y funcione sin conexion. Los datos (/api) siempre se piden al servidor.

const CACHE = 'nexora-v1';
const ARCHIVOS = [
    '/',
    '/index.html',
    '/css/app.css',
    '/js/app.js',
    '/manifest.json',
    '/icons/icon-192.png',
    '/icons/icon-512.png'
];

self.addEventListener('install', evento => {
    evento.waitUntil(caches.open(CACHE).then(cache => cache.addAll(ARCHIVOS)));
    self.skipWaiting();
});

self.addEventListener('activate', evento => {
    // Borra caches de versiones anteriores
    evento.waitUntil(
        caches.keys().then(claves =>
            Promise.all(claves.filter(c => c !== CACHE).map(c => caches.delete(c))))
    );
    self.clients.claim();
});

self.addEventListener('fetch', evento => {
    const url = new URL(evento.request.url);

    // La API nunca se guarda en cache: los datos deben estar al dia
    if (url.pathname.startsWith('/api/')) return;

    // Para la carcasa: primero la red, si no hay conexion se usa la cache
    evento.respondWith(
        fetch(evento.request)
            .then(resp => {
                const copia = resp.clone();
                caches.open(CACHE).then(cache => cache.put(evento.request, copia));
                return resp;
            })
            .catch(() => caches.match(evento.request).then(r => r || caches.match('/index.html')))
    );
});
