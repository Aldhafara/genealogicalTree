document.addEventListener("DOMContentLoaded", function () {
    const savedLanguage = localStorage.getItem("lang") || "en-EN";
    document.documentElement.lang = savedLanguage;

    changeLanguage(savedLanguage);
});

async function loadTranslations(language) {
    try {
        const response = await fetch(`/locales/${language}.json`);
        if (!response.ok) {
            throw new Error(`Could not load translations for language: ${language}`);
        }
        return await response.json();
    } catch (error) {
        console.error(error);
        return null;
    }
}

function formatFullName(firstName, lastName, unknown) {
    return `${firstName || unknown} ${lastName || unknown}`;
}

function formatName(name, unknown) {
    return `${name || unknown}`;
}

async function changeLanguage(language) {
    const translations = await loadTranslations(language);
    window.translations = translations;
    if (translations) {
        localStorage.setItem("lang", language);
        document.documentElement.lang = language;

        const elementsToTranslate = document.querySelectorAll("[data-i18n]");
        elementsToTranslate.forEach((element) => {
            const key = element.getAttribute("data-i18n");
            if (translations[key]) {
                element.textContent = translations[key];
            }
        });

        document.querySelectorAll("[data-i18n-format-fullName]").forEach(element => {
            const firstName = element.getAttribute("data-first-name");
            const lastName = element.getAttribute("data-last-name");
            element.textContent = formatFullName(firstName, lastName, translations["unknown"]);
        });

        document.querySelectorAll("[data-i18n-format-firstName]").forEach(element => {
            const firstName = element.getAttribute("data-first-name");
            element.textContent = formatName(firstName, translations["unknown"]);
        });

        document.querySelectorAll("[data-i18n-format-lastName]").forEach(element => {
            const lastName = element.getAttribute("data-last-name");
            element.textContent = formatName(lastName, translations["unknown"]);
        });
    }
}
