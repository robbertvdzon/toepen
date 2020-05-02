<template id="app-frame">
    <div class="app-frame">
        <header>
            <span><h1>Toepen met de vriendjes!</h1></span>
        </header>
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
