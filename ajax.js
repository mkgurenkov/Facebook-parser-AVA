const callback = arguments[arguments.length - 1];
const url = arguments[0];
const method = arguments[1];
const body = arguments[2];

options = {
    "content-type": "application/json; charset=UTF-8",
    "referer": 'https://adsmanager.facebook.com/',
    "origin": 'https://adsmanager.facebook.com',
    "mode": "cors",
    'method': method,
    'credentials': 'include'
};

if (!isNaN(body)) {
    options.body = body;
}

const fetchData = () => {
    return fetch(url, options).then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
    });
};

const timeoutPromise = (ms) => {
    return new Promise((_, reject) => setTimeout(() => reject(new Error("Timeout exceeded")), ms));
};

Promise.race([fetchData(), timeoutPromise(40000)])
    .then(response => callback({ success: true, response }))
    .catch(error => callback({ success: false, error: error.message }));