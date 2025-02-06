const HORIZONTAL_MARGIN = 50;
const VERTICAL_MARGIN = 120;
const RECTANGLE_WIDTH = 140;
const RECTANGLE_HEIGHT = 50;
const CHILD_SPACING = RECTANGLE_WIDTH + 10;

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
const backgroundGroup = mainGroup.append("g").attr("class", "background");
const foregroundGroup = mainGroup.append("g").attr("class", "foreground");

let lastCenteredNode = { x: 0, y: 0, scale: 1 };

function centerView(x, y, scale = 1) {
    lastCenteredNode = { x, y, scale };
    svg.transition().duration(750).call(
        zoom.transform,
        d3.zoomIdentity.translate(windowWidth / 2 - x * scale, windowHeight / 2 - y * scale).scale(scale)
    );
}

function drawParentRectangle(group, x, y, parent) {
    if (parent.mother || parent.father) {
        const baseY = y - RECTANGLE_HEIGHT / 2;
        const offsetY1 = RECTANGLE_HEIGHT / 5;
        const offsetY2 = 2 * RECTANGLE_HEIGHT / 5;
        const offsetX = RECTANGLE_WIDTH / 8;

        drawLine(backgroundGroup, x, baseY, x, baseY - offsetY1);
        drawLine(backgroundGroup, x - offsetX, baseY - offsetY1, x - offsetX, baseY - offsetY2);
        drawLine(backgroundGroup, x + offsetX, baseY - offsetY1, x + offsetX, baseY - offsetY2);
        drawLine(backgroundGroup, x + offsetX, baseY - offsetY1, x - offsetX, baseY - offsetY1);

        drawSmallRectangle(foregroundGroup, x - offsetX, baseY - offsetY2, parent.father.id);
        drawSmallRectangle(foregroundGroup, x + offsetX, baseY - offsetY2, parent.mother.id);
    }
    drawRectangle(foregroundGroup, x, y, getFullName(parent), parent.id);
}

function drawSmallRectangle(group, x, y, id) {
    const width = RECTANGLE_WIDTH / 8;
    const height = RECTANGLE_HEIGHT / 4;

    group.append("rect")
        .attr("x", x - width / 2)
        .attr("y", y - height)
        .attr("width", width)
        .attr("height", height)
        .attr("rx", 3)
        .attr("ry", 3)
        .attr("fill", "#fff")
        .attr("stroke", "#000")
        .attr("data-id", id)
        .style("cursor", "pointer")
        .on("click", () => {
            window.location.href = `/family-tree/${id}`;
        });
}

function drawRectangle(group, x, y, text, id) {
    const margin = 5;

    group.append("rect")
        .attr("x", x - RECTANGLE_WIDTH / 2)
        .attr("y", y - RECTANGLE_HEIGHT / 2)
        .attr("width", RECTANGLE_WIDTH)
        .attr("height", RECTANGLE_HEIGHT)
        .attr("rx", 10)
        .attr("ry", 10)
        .attr("fill", "#fff")
        .attr("stroke", "#000")
        .attr("data-id", id)
        .on("click", (event) => {
            if (event.detail === 1) {
                centerView(x, y);
            } else if (event.detail === 2) {
                window.location.href = `/family-tree/${id}`;
            }
        });

    group.append("text")
        .attr("x", x)
        .attr("y", y)
        .attr("text-anchor", "middle")
        .attr("alignment-baseline", "middle")
        .text(text);

    const linkX = x + RECTANGLE_WIDTH / 2 - margin;
    const linkY = y + RECTANGLE_HEIGHT / 2 - margin;
    const linkText = getDefaultValue("details");

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
    const familiesNumber = treeStructure.families.length;
    if (familiesNumber <= 1) {
        drawSingleTree(treeStructure.families[0], 0);
    } else {
        let childNumber = 0;
        let familyIndex = (RECTANGLE_HEIGHT / 10) * (1 - familiesNumber);
        const branchDirection = treeStructure.mainPersonSex == 'MALE' ? 1 : -1;
        let parentAlreadyDraw = false;

        for (const family of treeStructure.families) {
            const replacement = branchDirection * CHILD_SPACING * (childNumber + (family.children.length - 1) / 2);
            childNumber = childNumber + family.children.length;

            drawSingleTree(family, replacement, familyIndex, parentAlreadyDraw);
            parentAlreadyDraw = true;
            familyIndex = familyIndex + RECTANGLE_HEIGHT / 5;
        }
    }
}

function drawSingleTree(family, circleXPosition, circleYPosition = 0, parentAlreadyDraw = false) {
    const parentSpacing = 200;
    const radius = 5;

    const leftX = (!parentAlreadyDraw || circleXPosition < 0)
        ? (-parentSpacing / 2 + circleXPosition)
        : (-parentSpacing / 2);
    const rightX = (!parentAlreadyDraw || circleXPosition > 0)
        ? (parentSpacing / 2 + circleXPosition)
        : (parentSpacing / 2);

    const leftLineStart = leftX + RECTANGLE_WIDTH / 2;
    const rightLineStart = rightX - RECTANGLE_WIDTH / 2;

    drawLine(backgroundGroup, leftLineStart, circleYPosition, circleXPosition - radius, circleYPosition);
    drawLine(backgroundGroup, rightLineStart, circleYPosition, circleXPosition + radius, circleYPosition);

    if (!parentAlreadyDraw) {
        drawParentRectangle(foregroundGroup, leftX, 0, family.father);
        drawParentRectangle(foregroundGroup, rightX, 0, family.mother);
    } else if (circleXPosition > 0) {
        drawParentRectangle(foregroundGroup, rightX, 0, family.mother);
    } else if (circleXPosition < 0) {
        drawParentRectangle(foregroundGroup, leftX, 0, family.father);
    }

    mainGroup.append("circle")
        .attr("cx", circleXPosition)
        .attr("cy", circleYPosition)
        .attr("r", radius)
        .attr("fill", "#f0f0f0")
        .attr("stroke", "#000")
        .on("mouseover", function () {
            d3.select(this).append("title").text(family.marriageDate || getDefaultValue("noDate"));
        });

    if (family.children && family.children.length > 0) {
        const childY = 100;
        const yPosition = childY - 20;

        if (family.children.length === 1) {
            drawLine(backgroundGroup, 0 + circleXPosition, radius + circleYPosition, 0 + circleXPosition, yPosition);
            drawRectangle(foregroundGroup, 0 + circleXPosition, childY, getFullName(family.children[0]), family.children[0].id);
        } else {
            const lineLength = (family.children.length - 1) * CHILD_SPACING;
            const startX = -lineLength / 2;

            drawLine(backgroundGroup, 0 + circleXPosition, radius + circleYPosition, 0 + circleXPosition, yPosition / 2);

            drawLine(backgroundGroup, startX + circleXPosition, yPosition / 2, startX + lineLength + circleXPosition, yPosition / 2);

            family.children.forEach((child, index) => {
                const childX = startX + index * CHILD_SPACING;
                drawLine(backgroundGroup, childX + circleXPosition, yPosition, childX + circleXPosition, childY - 60);
                drawRectangle(foregroundGroup, childX + circleXPosition, childY, getFullName(child), child.id);
            });
        }
    }
}

function getFullName(child) {
    const firstName = child.firstName || getDefaultValue("unknown");
    const lastName = child.lastName || getDefaultValue("unknown");

    return firstName + " " + lastName;
}

function getDefaultValue(key) {
    if (!window.translations) {
        console.warn("translations are undefined")
        return "text";
    }
    return window.translations[key];
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
