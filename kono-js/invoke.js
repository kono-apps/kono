function invoke(name, params = {}) {
    return callToBackend((callbackId, errorId) => {
        const json = {
            "request": "function",
            "callbackId": callbackId,
            "errorId": errorId,
            "function": name,
            "passed": Object.keys(params),
            "data": params
        }
        window.ipc.postMessage(JSON.stringify(json))
    })
}
