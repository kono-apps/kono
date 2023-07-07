function emit(event, data = {}) {
    return callToBackend((callbackId, errorId) => {
        const json = {
            "request": "emitEvent",
            "event": event,
            "callbackId": callbackId,
            "errorId": errorId,
            "data": data
        }
        window.ipc.postMessage(JSON.stringify(json))
    })
}

function registerListener(event, callback) {
    return callToBackend((callbackId, errorId) => {
        const listenerCallbackId = createCallback((e) => {
            callback && callback(e)
        }, false)

        const json = {
            "request": "registerListener",
            "event": event,
            "listenerCallbackId": listenerCallbackId,
            "registerSuccessCallbackId": callbackId,
            "registerErrorCallbackId": errorId,
        }
        window.ipc.postMessage(JSON.stringify(json))
    })
}