// Caso de Complejidad Media — Sin Errores
// Requisitos: todo lo de baja + arreglo, >=2 clases, >=2 instanciaciones, >=2 funciones, >=2 llamadas

// Declaración de clases (>=2 clases)
class Calculadora {
    function sumar(a: integer, b: integer): integer {
        return a + b;
    }
    function multiplicar(a: integer, b: integer): integer {
        return a * b;
    }
}

class Persona {
    var nombre: string;
    function obtenerNombre(): string {
        return "Compiscript";
    }
}

// Declaraciones de funciones globales (>=2 funciones)
function calcularPromedio(total: integer, cantidad: integer): integer {
    return total / cantidad;
}

function generarSaludo(usuario: string): string {
    return "Bienvenido " + usuario;
}

// Variables básicas y arreglos
let total: integer = 100;
let contador: integer = 5;
var activo: boolean = true;
const MAX_ELEMENTOS: integer = 10;

// Arreglo (declaración y uso)
let numeros: integer[] = [10, 20, 30, 40, 50];
let primerElemento: integer = numeros[0];

// Instanciación de objetos (>=2 objetos)
let calc: Calculadora = new Calculadora();
let usuario: Persona = new Persona();

// Llamadas a función (>=2 llamadas)
let resSuma: integer = calc.sumar(total, 50);
let saludo: string = generarSaludo("Diego");

// Operaciones y estructuras de control
let promedio: integer = calcularPromedio(resSuma, 2);

if (promedio > 50) {
    print("Promedio alto");
} else {
    print("Promedio bajo");
}

while (contador > 0) {
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
