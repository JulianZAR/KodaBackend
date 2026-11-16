// Botón "ver/ocultar contraseña" de los campos de NutriGO.
document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('[data-toggle-password]').forEach(function (button) {
    button.addEventListener('click', function () {
      var input = document.getElementById(button.getAttribute('data-toggle-password'));
      if (!input) return;
      var visible = input.type === 'text';
      input.type = visible ? 'password' : 'text';
      button.classList.toggle('is-visible', !visible);
    });
  });
});
