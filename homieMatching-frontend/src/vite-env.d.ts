/// <reference types="vite/client" />
declare module '*.vue' {
    import Vue from "vue";
    export default Vue
}
declare module '*.vue' {
    import { DefineComponent } from "vue"
    const component: DefineComponent<{}, {}, any>
    export default component
}
