let i = 0;
let total = 0;

for (i = 0; i < 3; i = i + 1) {
    console.log("Iteration: " + i);
    total = total + i;
}

if (total > 2) {
    console.log("Total is big: " + total);
}