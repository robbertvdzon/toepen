<template id="app-frame">
    <div class="app-frame">
        <slot></slot>
    </div>
</template>
<script>
    Vue.component("app-frame", {template: "#app-frame"});
    $(document).ready(function () {
    });
</script>
<style>
    .app-frame > header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
</style>
