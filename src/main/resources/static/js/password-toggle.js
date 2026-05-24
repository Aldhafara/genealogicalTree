document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.toggle-password').forEach(button => {
        const targetId = button.dataset.target;
        const input = document.getElementById(targetId);
        const icon = button.querySelector('img');

        if (!targetId || !input || !icon) {
            return;
        }

        const eyeOpen = button.dataset.eyeOpen;
        const eyeClosed = button.dataset.eyeClosed;

        const showPassword = () => {
            input.type = 'text';
            button.setAttribute('aria-label', 'Hide password');
            button.setAttribute('aria-pressed', 'true');

            if (eyeClosed) {
                icon.src = eyeClosed;
            }
        };

        const hidePassword = () => {
            input.type = 'password';
            button.setAttribute('aria-label', 'Show password');
            button.setAttribute('aria-pressed', 'false');

            if (eyeOpen) {
                icon.src = eyeOpen;
            }
        };

        button.addEventListener('mousedown', showPassword);
        button.addEventListener('mouseup', hidePassword);
        button.addEventListener('mouseleave', hidePassword);

        button.addEventListener('touchstart', showPassword, { passive: true });
        button.addEventListener('touchend', hidePassword);
        button.addEventListener('touchcancel', hidePassword);

        button.addEventListener('blur', hidePassword);
    });
});
