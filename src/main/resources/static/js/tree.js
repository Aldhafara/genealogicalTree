const HORIZONTAL_MARGIN = 50;
const VERTICAL_MARGIN = 120;
const RECTANGLE_WIDTH = 140;

let windowWidth = window.innerWidth - 2 * HORIZONTAL_MARGIN;
let windowHeight = window.innerHeight  - 2 * VERTICAL_MARGIN;

let zoom = d3.zoom()
    .scaleExtent([0.5, 3])
    .on("zoom", event => mainGroup.attr("transform", event.transform));

const svg = d3.select("#tree")
    .append("svg")
    .attr("width", windowWidth)
    .attr("height", windowHeight)
    .call(zoom);

const mainGroup = svg.append("g");

let lastCenteredNode = { x: 0, y: 0, scale: 1 };

function centerView(x, y, scale = 1) {
    lastCenteredNode = { x, y, scale };
    svg.transition().duration(750).call(
        zoom.transform,
        d3.zoomIdentity.translate(windowWidth / 2 - x * scale, windowHeight / 2 - y * scale).scale(scale)
    );
}

function drawRectangle(group, x, y, text, id) {
    const height = 50;
    const margin = 5;

    const rect = group.append("rect")
        .attr("x", x - RECTANGLE_WIDTH / 2)
        .attr("y", y - height / 2)
        .attr("width", RECTANGLE_WIDTH)
        .attr("height", height)
        .attr("rx", 10)
        .attr("ry", 10)
        .attr("fill", "#fff")
        .attr("stroke", "#000")
        .attr("data-id", id)
        .on("click", () => centerView(x, y));

    group.append("text")
        .attr("x", x)
        .attr("y", y)
        .attr("text-anchor", "middle")
        .attr("alignment-baseline", "middle")
        .text(text);

    const linkX = x + RECTANGLE_WIDTH / 2 - margin;
    const linkY = y + height / 2 - margin;
    const linkText = window.translations["details"];

    group.append("a")
        .attr("href", `/person/${id}`)
        .attr("target", "_blank")
        .append("text")
        .attr("x", linkX)
        .attr("y", linkY)
        .attr("text-anchor", "end")
        .attr("alignment-baseline", "bottom")
        .attr("font-size", "12px")
        .attr("fill", "blue")
        .style("cursor", "pointer")
        .text(linkText);
}

function drawLine(group, x1, y1, x2, y2) {
    group.append("line")
        .attr("x1", x1)
        .attr("y1", y1)
        .attr("x2", x2)
        .attr("y2", y2)
        .attr("stroke", "#000")
        .attr("stroke-width", 2);
}

function drawTree(treeStructure) {
    const parentSpacing = 200;

    if (treeStructure.families.length <= 1) {
        drawSingleTree(treeStructure.families[0], 0);
    } else {
        drawSingleTree(treeStructure.families[0], -200);
        drawSingleTree(treeStructure.families[1], 200);
    }
}

function drawSingleTree(family, circleXPosition) {
    const parentSpacing = 200;

    drawRectangle(mainGroup, -parentSpacing / 2 + circleXPosition, 0, getFullName(family.father), family.father.id);
    drawRectangle(mainGroup, parentSpacing / 2 + circleXPosition, 0, getFullName(family.mother), family.mother.id);

    const radius = 5;
    mainGroup.append("circle")
        .attr("cx", circleXPosition)
        .attr("cy", 0)
        .attr("r", radius)
        .attr("fill", "#f0f0f0")
        .attr("stroke", "#000")
        .on("mouseover", function () {
            d3.select(this).append("title").text(family.marriageDate || window.translations["noDate"]);
        });

    drawLine(mainGroup, -parentSpacing / 2 + RECTANGLE_WIDTH / 2 + circleXPosition, 0, circleXPosition - radius, 0);
    drawLine(mainGroup, parentSpacing / 2 - RECTANGLE_WIDTH / 2 + circleXPosition, 0, circleXPosition + radius, 0);

    if (family.children && family.children.length > 0) {
        const childY = 100;
        const childSpacing = RECTANGLE_WIDTH + 10;
        const yPosition = childY - 20;

        if (family.children.length === 1) {
            drawLine(mainGroup, 0 + circleXPosition, radius, 0 + circleXPosition, yPosition);
            drawRectangle(mainGroup, 0 + circleXPosition, childY, getFullName(family.children[0]), family.children[0].id);
        } else {
            const lineLength = (family.children.length - 1) * childSpacing;
            const startX = -lineLength / 2;

            drawLine(mainGroup, 0 + circleXPosition, radius, 0 + circleXPosition, yPosition / 2);

            drawLine(mainGroup, startX + circleXPosition, yPosition / 2, startX + lineLength + circleXPosition, yPosition / 2);

            family.children.forEach((child, index) => {
                const childX = startX + index * childSpacing;
                drawLine(mainGroup, childX + circleXPosition, yPosition, childX + circleXPosition, childY - 60);
                drawRectangle(mainGroup, childX + circleXPosition, childY, getFullName(child), child.id);
            });
        }
    }
}

function getFullName(child) {
    const firstName = child.firstName || window.translations["unknown"];
    const lastName = child.lastName || window.translations["unknown"];

    return firstName + " " + lastName;
}

async function fetchMyId() {
    const url = `/api/family-tree/get-my-id`;

    try {
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        return data.id;
    } catch (error) {
        console.error("Error fetching UUID:", error);
        throw error;
    }
}

async function fetchTreeStructure(id) {
    const url = `/api/family-tree/get-structure/${id}`;

    try {
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Error fetching tree structure:", error);
        throw error;
    }
}

async function initializeTree(initialUuid = null) {
    try {
        if (!initialUuid) {
            initialUuid = await fetchMyId();
        }
        familyTreeData = await fetchTreeStructure(initialUuid);
        drawTree(familyTreeData);
        const initialNode = mainGroup.select(`[data-id='${initialUuid}']`);
        if (!initialNode.empty()) {
            const bbox = initialNode.node().getBBox();
            centerView(bbox.x + bbox.width / 2, bbox.y + bbox.height / 2);
        }
    } catch (error) {
        console.error("Error initializing tree:", error);
    }
}

const treeElement = document.getElementById("tree");
const initialUuid = treeElement.getAttribute("data-initial-id");

initializeTree(initialUuid);

window.addEventListener("resize", () => {
    windowWidth = window.innerWidth - 2 * HORIZONTAL_MARGIN;
    windowHeight = window.innerHeight  - 2 * VERTICAL_MARGIN;

    svg.attr("width", windowWidth).attr("height", windowHeight);

    centerView(lastCenteredNode.x, lastCenteredNode.y, lastCenteredNode.scale);
});
