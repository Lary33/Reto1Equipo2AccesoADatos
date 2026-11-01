// Menú estilo Space Invaders
//
// Ejemplo de uso:
//   iniciarMenuInvaders({
//     contenedor: '#invaders-menu',
//     titulo: 'Elige entidad',
//     invasores: [
//       { etiqueta: 'Clientes', destino: 'clientes.html' },     // destino = una URL
//       { etiqueta: 'Pedidos',  destino: function () { ... } }  // o una función
//     ]
//   });
//
// Controles: ratón (mover y clic para disparar), flechas o A/D para mover
// y espacio para disparar. Con Tab + Enter también se puede entrar sin jugar.

// Valores del juego (se pueden cambiar aquí)
const VELOCIDAD_BALA = 700;     // píxeles por segundo
const VELOCIDAD_NAVE = 600;     // píxeles por segundo (teclado)
const ESPERA_DISPARO = 350;     // milisegundos entre disparos
const TIEMPO_EXPLOSION = 500;   // milisegundos antes de cambiar de pantalla
const BALA_ANCHO = 4;           // tienen que coincidir con .im-bala del CSS
const BALA_ALTO = 16;

// Dibujos en pixel art: cada # es un píxel
const DIBUJOS_INVASORES = [
  [
    '...##...',
    '..####..',
    '.######.',
    '##.##.##',
    '########',
    '..#..#..',
    '.#.##.#.',
    '#.#..#.#'
  ],
  [
    '..#.....#..',
    '...#...#...',
    '..#######..',
    '.##.###.##.',
    '###########',
    '#.#######.#',
    '#.#.....#.#',
    '...##.##...'
  ],
  [
    '....####....',
    '.##########.',
    '############',
    '###..##..###',
    '############',
    '...##..##...',
    '..##.##.##..',
    '##........##'
  ]
];

const DIBUJO_NAVE = [
  '.....#.....',
  '....#.#....',
  '...#...#...',
  '.####.####.',
  '#...###...#',
  '##.#####.##',
  '.###...###.',
  '..#.....#..',
];

// Convierte un dibujo de texto en un SVG (un cuadradito por cada #)
function dibujarSprite(dibujo) {
  let trazado = '';
  for (let y = 0; y < dibujo.length; y++) {
    for (let x = 0; x < dibujo[y].length; x++) {
      if (dibujo[y][x] === '#') {
        trazado += 'M' + x + ' ' + y + 'h1v1h-1z';
      }
    }
  }
  return '<svg class="im-sprite" viewBox="0 0 ' + dibujo[0].length + ' ' + dibujo.length + '"' +
         ' shape-rendering="crispEdges" aria-hidden="true">' +
         '<path d="' + trazado + '" fill="currentColor"/></svg>';
}

function iniciarMenuInvaders(opciones) {
  const contenedor = document.querySelector(opciones.contenedor);
  const titulo = opciones.titulo || '';
  const listaInvasores = opciones.invasores;

  // Si ya había un menú en este contenedor, lo paramos antes de crear el nuevo
  if (contenedor.detenerMenu) {
    contenedor.detenerMenu();
  }

  // ---------- Crear el HTML ----------
  contenedor.innerHTML = '';

  const arena = document.createElement('div');
  arena.className = 'im-arena';
  contenedor.appendChild(arena);

  if (titulo) {
    const h1 = document.createElement('h1');
    h1.className = 'im-titulo';
    h1.textContent = titulo;
    arena.appendChild(h1);
  }

  // Todos los invasores van en una única fila
  const fila = document.createElement('div');
  fila.className = 'im-fila';
  arena.appendChild(fila);

  const invasores = [];   // aquí guardamos { datos, boton } de cada invasor

  for (let i = 0; i < listaInvasores.length; i++) {
    const datos = listaInvasores[i];

    const boton = document.createElement('button');
    boton.type = 'button';
    boton.className = 'im-invasor';
    boton.setAttribute('aria-label', datos.etiqueta);

    if (datos.icono) {
      boton.innerHTML = '<img class="im-icono" src="' + datos.icono + '" alt="" draggable="false">';
    } else {
      boton.innerHTML = dibujarSprite(DIBUJOS_INVASORES[i % DIBUJOS_INVASORES.length]);
    }

    const etiqueta = document.createElement('span');
    etiqueta.className = 'im-etiqueta';
    etiqueta.textContent = datos.etiqueta;
    boton.appendChild(etiqueta);

    // Con el ratón hay que disparar al invasor, el clic normal no hace nada.
    // Con el teclado (Tab + Enter) detail vale 0 y entra directamente.
    boton.addEventListener('click', function (e) {
      if (e.detail === 0) {
        impacto(datos, boton);
      } else {
        e.preventDefault();
      }
    });

    fila.appendChild(boton);
    invasores.push({ datos: datos, boton: boton });
  }

  const nave = document.createElement('div');
  nave.className = 'im-nave';
  nave.innerHTML = dibujarSprite(DIBUJO_NAVE);
  arena.appendChild(nave);

  const ayuda = document.createElement('p');
  ayuda.className = 'im-ayuda';
  ayuda.textContent = 'Mueve el ratón y haz clic para disparar · Tab + Enter para usar el teclado';
  arena.appendChild(ayuda);

  // ---------- Variables del juego ----------
  let naveX = arena.clientWidth / 2;   // centro de la nave dentro de la arena
  let balas = [];                      // cada bala: { el, x, y }
  let ultimoDisparo = 0;
  let ultimoFrame = performance.now();
  let bloqueado = false;               // true cuando ya hemos acertado a un invasor
  let moviendoIzquierda = false;
  let moviendoDerecha = false;
  let animacion = 0;
  let temporizador = 0;

  // ---------- Nave ----------
  function moverNave() {
    const mitad = nave.offsetWidth / 2;
    // que no se salga de la pantalla
    if (naveX < mitad) naveX = mitad;
    if (naveX > arena.clientWidth - mitad) naveX = arena.clientWidth - mitad;
    // solo cambia la X, la nave nunca se mueve en vertical
    nave.style.transform = 'translateX(' + (naveX - mitad) + 'px)';
  }

  // ---------- Disparo ----------
  function disparar() {
    const ahora = performance.now();
    if (bloqueado || ahora - ultimoDisparo < ESPERA_DISPARO) {
      return;
    }
    ultimoDisparo = ahora;

    const el = document.createElement('div');
    el.className = 'im-bala';
    arena.appendChild(el);

    const bala = {
      el: el,
      x: naveX - BALA_ANCHO / 2,
      y: nave.offsetTop - BALA_ALTO
    };
    el.style.transform = 'translate(' + bala.x + 'px,' + bala.y + 'px)';
    balas.push(bala);
  }

  // ---------- Colisión ----------
  // Devuelve el invasor que toca la bala, o null si no toca ninguno
  function buscarInvasorTocado(bala, rectArena) {
    for (let i = 0; i < invasores.length; i++) {
      const r = invasores[i].boton.getBoundingClientRect();
      // pasamos la posición del invasor a coordenadas de la arena
      const izquierda = r.left - rectArena.left;
      const arriba = r.top - rectArena.top;

      const tocaEnX = bala.x < izquierda + r.width && bala.x + BALA_ANCHO > izquierda;
      const tocaEnY = bala.y < arriba + r.height && bala.y + BALA_ALTO > arriba;

      if (tocaEnX && tocaEnY) {
        return invasores[i];
      }
    }
    return null;
  }

  // ---------- Impacto ----------
  function impacto(datos, boton) {
    if (bloqueado) return;
    bloqueado = true;
    moviendoIzquierda = false;
    moviendoDerecha = false;

    boton.classList.add('im-impactado');       // animación de explosión
    arena.classList.add('im-bloqueada');       // para la marcha de los invasores

    temporizador = setTimeout(function () {
      irAlDestino(datos);
    }, TIEMPO_EXPLOSION);
  }

  function irAlDestino(datos) {
    if (typeof datos.destino === 'function') {
      detener();            // quitamos este menú antes de abrir el siguiente
      datos.destino();
    } else if (datos.destino) {
      window.location.href = datos.destino;
    } else {
      desbloquear();        // sin destino: se puede seguir jugando
    }
  }

  function desbloquear() {
    clearTimeout(temporizador);
    bloqueado = false;
    arena.classList.remove('im-bloqueada');
    for (let i = 0; i < invasores.length; i++) {
      invasores[i].boton.classList.remove('im-impactado');
    }
  }

  // ---------- Bucle del juego (se repite en cada frame) ----------
  function bucle(ahora) {
    // segundos desde el frame anterior (con un máximo por si la pestaña estuvo parada)
    const dt = Math.min((ahora - ultimoFrame) / 1000, 0.05);
    ultimoFrame = ahora;

    if (moviendoIzquierda) naveX -= VELOCIDAD_NAVE * dt;
    if (moviendoDerecha) naveX += VELOCIDAD_NAVE * dt;
    moverNave();

    const rectArena = arena.getBoundingClientRect();

    // recorremos las balas de atrás hacia delante para poder borrar sin problemas
    for (let i = balas.length - 1; i >= 0; i--) {
      const bala = balas[i];
      bala.y -= VELOCIDAD_BALA * dt;

      let tocado = null;
      if (!bloqueado) {
        tocado = buscarInvasorTocado(bala, rectArena);
      }

      if (tocado) {
        bala.el.remove();
        balas.splice(i, 1);
        impacto(tocado.datos, tocado.boton);
      } else if (bala.y + BALA_ALTO < 0) {
        bala.el.remove();           // salió por arriba
        balas.splice(i, 1);
      } else {
        bala.el.style.transform = 'translate(' + bala.x + 'px,' + bala.y + 'px)';
      }
    }

    animacion = requestAnimationFrame(bucle);
  }

  // ---------- Controles ----------
  function alMoverRaton(e) {
    naveX = e.clientX - arena.getBoundingClientRect().left;
  }

  function alHacerClic(e) {
    if (e.button !== 0) return;
    // en pantallas táctiles no hay movimiento previo: colocamos la nave donde se toca
    if (e.pointerType !== 'mouse') {
      naveX = e.clientX - arena.getBoundingClientRect().left;
      moverNave();
    }
    disparar();
  }

  function alPulsarTecla(e) {
    if (e.code === 'ArrowLeft' || e.code === 'KeyA') {
      moviendoIzquierda = true;
      e.preventDefault();
    } else if (e.code === 'ArrowRight' || e.code === 'KeyD') {
      moviendoDerecha = true;
      e.preventDefault();
    } else if (e.code === 'Space' || e.code === 'ArrowUp' || e.code === 'KeyW') {
      // si hay un invasor enfocado, el espacio lo activa en vez de disparar
      const enfocado = document.activeElement;
      if (e.code === 'Space' && enfocado && enfocado.classList.contains('im-invasor')) {
        return;
      }
      e.preventDefault();
      disparar();
    }
  }

  function alSoltarTecla(e) {
    if (e.code === 'ArrowLeft' || e.code === 'KeyA') moviendoIzquierda = false;
    if (e.code === 'ArrowRight' || e.code === 'KeyD') moviendoDerecha = false;
  }

  // Si se vuelve con el botón "atrás", la página puede reaparecer con el juego bloqueado
  function alMostrarPagina(e) {
    if (e.persisted) desbloquear();
  }

  arena.addEventListener('pointermove', alMoverRaton);
  arena.addEventListener('pointerdown', alHacerClic);
  document.addEventListener('keydown', alPulsarTecla);
  document.addEventListener('keyup', alSoltarTecla);
  window.addEventListener('pageshow', alMostrarPagina);

  // ---------- Parar el menú ----------
  function detener() {
    cancelAnimationFrame(animacion);
    clearTimeout(temporizador);
    document.removeEventListener('keydown', alPulsarTecla);
    document.removeEventListener('keyup', alSoltarTecla);
    window.removeEventListener('pageshow', alMostrarPagina);
    contenedor.innerHTML = '';
    contenedor.detenerMenu = null;
  }

  contenedor.detenerMenu = detener;

  // ---------- Empezar ----------
  moverNave();
  animacion = requestAnimationFrame(bucle);
}
