// Caso de Complejidad Media — Errores Sintácticos
// Requisitos: >=3 errores sintácticos, 0 errores léxicos

class Calculadora {
    function sumar(a: integer, b: integer): integer {
        return a + b;
    }
    function multiplicar(a: integer, b: integer): integer {
        return a * b
    }
}

class Persona {
    var nombre: string;
    function obtenerNombre(): string {
        return "Compiscript";
    }
}

function calcularPromedio(total: integer, cantidad: integer): integer {
    return total / cantidad;
}

function generarSaludo(usuario: string): string {
    return "Bienvenido " + usuario;
}

let total: integer = 100
let contador: integer = 5;
var activo: boolean = true;
const MAX_ELEMENTOS: integer = 10;

let numeros: integer[] = [10, 20, 30, 40, 50];
let primerElemento: integer = numeros[0];

let calc: Calculadora = new Calculadora();
let usuario: Persona = new Persona();

let resSuma: integer = calc.sumar(total, 50);
let saludo: string = generarSaludo("Diego");

let promedio: integer = calcularPromedio(resSuma, 2);

if (promedio > 50) {
    print("Promedio alto");
} else {
    print("Promedio bajo");
}

while contador > 0 {
    contador = contador - 1;
}

for (let i: integer = 0; i < 3; i = i + 1) {
    print(i);
}

switch (promedio) {
    case 75:
        print("Setenta y cinco");
        break;
    default:
        print("Otro promedio");
}

foreach (num in numeros) {
    print(num);
}
