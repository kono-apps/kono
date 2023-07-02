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

function invoke(name, params = {}) {
    return new Promise((resolve, reject) => {
        const callbackId = createCallback((result) => {
            resolve(result)

            // success, we don't need the failed callback
            delete window[`_${errorId}`]
        }, true)
        const errorId = createCallback((e) => {
            reject(e)

            // failed, we don't need the success callback
            delete window[`_${callbackId}`]
        }, true)
        const json = {
            "callbackId": callbackId, /* callback */
            "errorId": errorId, /* error */
            "function": name, /* function name */
            "passed": Object.keys(params), /* passed parameters */
            "data": params /* data */
        }

        window.ipc.postMessage(JSON.stringify(json))
    });
}
