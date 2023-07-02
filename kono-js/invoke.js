function uid() {
    return window.crypto.getRandomValues(new Uint32Array(1))[0]
}

window.ipc = undefined;

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

async function invoke(name, params) {
    return new Promise((resolve, reject) => {
        const result = createCallback((result) => {
            resolve(result)

            // success, we don't need the failed callback
            delete window[`_${failed}`]
        }, true)
        const failed = createCallback((e) => {
            reject(e)

            // failed, we don't need the success callback
            delete window[`_${result}`]
        }, true)
        const json = {
            "c": result, /* callback */
            "e": failed, /* error */
            "fn": name, /* function name */
            "p": Object.keys(params), /* passed parameters */
            "d": params /* data */
        }

        window.ipc.postMessage(JSON.stringify(json))
    });
}
