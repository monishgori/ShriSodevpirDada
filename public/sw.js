const CACHE_NAME = 'sodev-pooja-v1.0.8';

// Install Event
self.addEventListener('install', (event) => {
    self.skipWaiting();
});

// Activate Event
self.addEventListener('activate', (event) => {
    event.waitUntil(
        caches.keys().then((keys) => {
            return Promise.all(
                keys.map((key) => caches.delete(key))
            );
        })
    );
});

// Fetch Event - DISABLE INTERCEPTION TEMPORARILY
self.addEventListener('fetch', (event) => {
    // Just let it pass through to the network
    return;
});
