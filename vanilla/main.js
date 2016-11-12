const tileSize = 16;

class World {
    constructor(w, h) {
        this.width = w;
        this.height = h;
        this._fields = [];
    }

    addField(field) {
        this._fields.push(field);
    }

    draw(ctx) {
        for (let y = 0; y < this.height; y += tileSize) {
            for (let x = 0; x < this.width; x += tileSize) {
                ctx.fillStyle = this.potentialColor(x, y);
                ctx.fillRect(x, y, tileSize, tileSize);
            }
        }
    }

    potentialColor(x, y) {
        let potential = this.potentialAt(x, y);
        return (potential > 0)
            ? rgba(0, potential, 0, 1)
            : rgba(-potential, 0, 0, 1);
    }

    potentialAt(x, y) {
        return this._fields.reduce(function(potential, field) {
            return potential + field.potentialAt(x, y);
        }, 0);
    }
}

function main() {
    const canvas = document.createElement("canvas");
    const ctx = canvas.getContext("2d");
    const world = new World(1280, 720);

    world.addField(new PotentialField({
        x: 200,
        y: 200,
        potential: -256,
        gradation: 64,
    }));

    world.addField(new PotentialField({
        x: 600,
        y: 100,
        potential: 256,
        gradation: 8,
    }));

    world.addField(new PotentialField({
        x: 400,
        y: 300,
        potential: -256,
        gradation: 128,
    }));

    const player = {x: 0, y: 0};

    canvas.width = world.width;
    canvas.height = world.height;
    document.body.appendChild(canvas);

    requestAnimationFrame(tick);


    function tick() {
        update();
        draw();
        requestAnimationFrame(tick);
    }

    function draw() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        world.draw(ctx);
    }

    function update() {
    }
}

class PotentialField {
    constructor({x, y, potential, gradation}) {
        this.x = x;
        this.y = y;
        this.potential = potential;
        this.gradation = gradation;
    }

    potentialAt(x, y) {
        let distance = Math.hypot(this.x - x, this.y - y);
        return (this.potential > 0)
            ? Math.max(0, Math.round(this.potential - (distance / tileSize) * this.gradation))
            : Math.min(0, Math.round(this.potential + (distance / tileSize) * this.gradation));
    }
}

function rgba(r, g, b, a) {
    return `rgba(${r}, ${g}, ${b}, ${a})`;
}

main();
