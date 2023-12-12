import {callToBackend, createCallback} from "./callback";

export async function emit<T>(id: string, event: T): Promise<void> {
    return callToBackend((callbackId, errorId) => {
        const json = {
            "type": "emitEvent",
            "event": id,
            "callbackId": callbackId,
            "errorId": errorId,
            "data": event
        }
        // @ts-ignore
        window.ipc.postMessage(JSON.stringify(json))
    })
}

export async function registerListener<T>(event: string, callback: (event: T) => void): Promise<void> {
    return callToBackend((callbackId, errorId) => {
        const listenerCallbackId = createCallback((e) => {
            callback && callback(e)
        }, false)

        const json = {
            "type": "registerListener",
            "event": event,
            "listenerCallbackId": listenerCallbackId,
            "registerSuccessCallbackId": callbackId,
            "registerErrorCallbackId": errorId,
        }
        // @ts-ignore
        window.ipc.postMessage(JSON.stringify(json))
    })
}
