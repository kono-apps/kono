/**
 * Generates a random integer UID for creating unique
 * identifiers
 */
function uid(): number {
    return window.crypto.getRandomValues(new Uint32Array(1))[0]
}

/**
 * Creates a unique function bound to the global window
 *
 * @param callback The content of the function
 * @param deleteAfterFinished If the function
 */
function createCallback(
    callback?: (response: any) => void,
    deleteAfterFinished: boolean = false
): number {
    let callbackId = uid()
    Object.defineProperty(window, `_${callbackId}`, {
        value: (result: any) => {
            if (deleteAfterFinished) {
                Reflect.deleteProperty(window, `_${callbackId}`)
            }
            callback && callback(result)
        },
        writable: false,
        configurable: true
    })
    return callbackId
}

function callToBackend<T>(callback: (callbackId: number, errorId: number) => void): Promise<T> {
    return new Promise<T>((resolve, reject) => {
        const callbackId = createCallback((v) => {
            resolve(v)

            // success, we don't need the failed callback
            Reflect.deleteProperty(window, `_${errorId}`)
        }, true)

        const errorId = createCallback((e) => {
            reject(e)

            // failed, we don't need the success callback
            Reflect.deleteProperty(window, `_${callbackId}`)
        }, true)

        callback(callbackId, errorId)
    })
}

export {callToBackend, createCallback}