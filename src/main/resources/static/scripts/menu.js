document.addEventListener("DOMContentLoaded", function () {
    const hamburger = document.querySelector(".hamburger-menu");
    const dropdown = document.getElementById("hamburgerDropdown");

    hamburger.addEventListener("click", function () {
        dropdown.style.display = (dropdown.style.display === "block") ? "none" : "block";
    });

    document.addEventListener("click", function (event) {
        if (!hamburger.contains(event.target) && !dropdown.contains(event.target)) {
            dropdown.style.display = "none";
        }
    });
});
