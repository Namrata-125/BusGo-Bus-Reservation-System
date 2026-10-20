const API_BASE_URL = "http://localhost:8080/api";


/* ================= GET SELECTED ROUTE ================= */

const routeId =
    localStorage.getItem("busgo_selected_route");


const routes =
    JSON.parse(
        localStorage.getItem("busgo_routes") || "[]"
    );


const selectedRoute =
    routes.find(function (route) {

        return String(route.routeId) ===
               String(routeId);

    });


/* ================= ELEMENTS ================= */

const seatContainer =
    document.getElementById("seatContainer");

const busDetails =
    document.getElementById("busDetails");

const selectedSeatText =
    document.getElementById("selectedSeatText");

const continueButton =
    document.getElementById("continueButton");


let selectedSeat = null;


/* ================= CHECK ROUTE ================= */

if (!selectedRoute) {

    seatContainer.innerHTML = `

        <div class="alert alert-danger text-center">

            Selected bus information was not found.

        </div>

    `;

    throw new Error("Selected route not found.");

}


const bus =
    selectedRoute.bus;


/* ================= BUS DETAILS ================= */

busDetails.innerHTML = `

    <h5 class="fw-bold">

        ${bus.busName}

    </h5>

    <p class="text-muted mb-3">

        ${bus.operatorName}

    </p>


    <div class="mb-2">

        <i class="bi bi-bus-front text-primary"></i>

        ${bus.busNumber}

    </div>


    <div class="mb-2">

        <i class="bi bi-tag text-primary"></i>

        ${bus.busType}

    </div>


    <div class="mb-2">

        <i class="bi bi-geo-alt text-primary"></i>

        ${selectedRoute.source}
        →
        ${selectedRoute.destination}

    </div>


    <div class="mb-2">

        <i class="bi bi-currency-rupee text-primary"></i>

        Fare:
        <strong>₹${selectedRoute.fare}</strong>

    </div>

`;


/* ================= LOAD SEATS ================= */

async function loadSeats() {

    try {

        const response = await fetch(
            `${API_BASE_URL}/seats/bus/${bus.busId}`
        );


        if (!response.ok) {

            throw new Error(
                "Unable to load seats."
            );

        }


        const seats =
            await response.json();


        displaySeats(seats);

    }

    catch (error) {

        console.error(
            "Seat loading error:",
            error
        );


        seatContainer.innerHTML = `

            <div class="alert alert-danger text-center">

                Unable to load seats.
                Please make sure the backend is running.

            </div>

        `;

    }

}


/* ================= DISPLAY SEATS ================= */

function displaySeats(seats) {

    if (seats.length === 0) {

        seatContainer.innerHTML = `

            <div class="alert alert-warning text-center">

                No seats are available for this bus.

            </div>

        `;

        return;

    }


    seatContainer.innerHTML = "";


    /*
     * Four seats per row:
     *
     * 1  2    3  4
     * 5  6    7  8
     *
     * Middle gap represents the bus aisle.
     */


    for (
        let i = 0;
        i < seats.length;
        i += 4
    ) {

        const row =
            document.createElement("div");

        row.className =
            "seat-row";


        for (
            let j = i;
            j < i + 4 && j < seats.length;
            j++
        ) {

            const seat =
                seats[j];


            const seatButton =
                document.createElement("button");


            seatButton.type =
                "button";


            seatButton.textContent =
                seat.seatNumber;


            seatButton.classList.add(
                "seat"
            );


            /* Booked seat */

            if (
                seat.seatStatus === "BOOKED"
            ) {

                seatButton.classList.add(
                    "booked"
                );

                seatButton.disabled = true;

            }


            /* Available seat */

            else {

                seatButton.classList.add(
                    "available"
                );


                seatButton.addEventListener(
                    "click",
                    function () {

                        selectSeat(
                            seat,
                            seatButton
                        );

                    }
                );

            }


            /*
             * Add aisle gap before
             * the third seat.
             */

            if (j === i + 2) {

                seatButton.style.marginLeft =
                    "25px";

            }


            row.appendChild(
                seatButton
            );

        }


        seatContainer.appendChild(
            row
        );

    }

}


/* ================= SELECT SEAT ================= */

function selectSeat(
    seat,
    seatButton
) {

    /* Remove previous selection */

    document
        .querySelectorAll(".seat.selected")
        .forEach(function (button) {

            button.classList.remove(
                "selected"
            );

            button.classList.add(
                "available"
            );

        });


    /* Select new seat */

    seatButton.classList.remove(
        "available"
    );

    seatButton.classList.add(
        "selected"
    );


    selectedSeat =
        seat;


    selectedSeatText.textContent =
        seat.seatNumber;


    continueButton.disabled =
        false;

}


/* ================= CONTINUE ================= */

continueButton.addEventListener(
    "click",
    function () {

        if (!selectedSeat) {

            return;

        }


        localStorage.setItem(
            "busgo_selected_seat",
            JSON.stringify(selectedSeat)
        );


        window.location.href =
            "booking.html";

    }
);


/* ================= START ================= */

loadSeats();