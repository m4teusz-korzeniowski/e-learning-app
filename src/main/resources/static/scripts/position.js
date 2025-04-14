window.addEventListener('beforeunload', function() {
    sessionStorage.setItem('scrollPosition', window.scrollY);
});

window.addEventListener('load', function() {
    const scrollPosition = sessionStorage.getItem('scrollPosition');
    if (scrollPosition) {
        window.scrollTo(0, scrollPosition);
        sessionStorage.removeItem('scrollPosition');
    }
});