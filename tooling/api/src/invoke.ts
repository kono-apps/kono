// @ts-ignore

import {callToBackend} from "./callback";

declare global {
    interface Window {
        ipc: {
            postMessage: (args: string) => void
        }
    }
}

export type InvokeArgs = Record<string, unknown>

export async function invoke<T>(name: string, args: InvokeArgs): Promise<T> {
    return callToBackend<T>((callbackId, errorId) => {
        const json = {
            "request": "function",
            "callbackId": callbackId,
            "errorId": errorId,
            "function": name,
            "passed": Object.keys(args),
            "data": args
        }
        // @ts-ignore
        window.ipc.postMessage(JSON.stringify(json))
    })
}
