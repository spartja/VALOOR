document.addEventListener("click", (event) => {
    const toggle = event.target.closest("[data-password-toggle]");
    if (!toggle) return;
    const input = document.getElementById(toggle.dataset.passwordToggle);
    if (!input) return;
    const showing = input.type === "password";
    input.type = showing ? "text" : "password";
    toggle.textContent = showing ? "Ocultar" : "Mostrar";
});

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-dismiss]").forEach((notice) => {
        window.setTimeout(() => notice.remove(), 6000);
    });
});
