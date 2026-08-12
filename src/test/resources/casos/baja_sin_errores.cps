// Caso de Complejidad Baja — Sin Errores
// Requisitos: variables de >=3 tipos, const, >=2 ops aritméticas, if-else, bucle, switch/foreach

let contador: integer = 10;
var mensaje: string = "Prueba de Compiscript";
let esValido: boolean = true;
const LIMITE: integer = 100;

// Operaciones aritméticas (+, *)
let suma: integer = contador + 5;
let producto: integer = contador * 2;

// Estructura condicional if-else
if (suma < LIMITE) {
    print("Suma dentro del limite");
} else {
    print("Suma supera el limite");
}

// Bucle while
while (contador > 0) {
    contador = contador - 1;
}

// Bucle for
for (let i: integer = 0; i < 3; i = i + 1) {
    print(i);
}

// Switch-Case y Foreach
switch (producto) {
    case 20:
        print("Es veinte");
        break;
    default:
        print("Otro valor");
}

foreach (letra in mensaje) {
    print(letra);
}
