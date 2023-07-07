function uid() {
    return window.crypto.getRandomValues(new Uint32Array(1))[0]
}

function createCallback(callback, deleteAfterFinished) {
    let callbackId = uid()
    Object.defineProperty(window, `_${callbackId}`, {
        value: (result) => {
            if (deleteAfterFinished)
                Reflect.deleteProperty(window, `_${callbackId}`)
            callback && callback(result)
        },
        writable: false,
        configurable: true
    })
    return callbackId
}

function callToBackend(callback) {
    return new Promise((resolve, reject) => {
        const callbackId = createCallback((v) => {
            resolve(v)

            // success, we don't need the failed callback
            delete window[`_${errorId}`]
        }, true)

        const errorId = createCallback((e) => {
            reject(e)

            // failed, we don't need the success callback
            delete window[`_${callbackId}`]
        }, true)

        callback(callbackId, errorId)
    })
}