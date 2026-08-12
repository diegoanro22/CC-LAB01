// Caso de Complejidad Baja — Errores Mixtos
// Requisitos: >=2 errores léxicos y >=2 errores sintácticos

let @ contador: integer = 10
var # mensaje: string = "Prueba de Compiscript";
let esValido: boolean = true;
const LIMITE: integer = 100;

let suma: integer = contador + 5;
let producto: integer = contador * 2;

if suma < LIMITE {
    print("Suma dentro del limite");
} else {
    print("Suma supera el limite");
}

while (contador > 0) {
    contador = contador - 1;
}

for (let i: integer = 0; i < 3; i = i + 1) {
    print(i);
}

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
