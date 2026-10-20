const API_BASE_URL = "http://localhost:8080/api";

/* ==================================================
   GET SAVED DATA
   ================================================== */

const routeId =
    localStorage.getItem("busgo_selected_route");

const selectedSeat =
    JSON.parse(
        localStorage.getItem("busgo_selected_seat") || "null"
    );

const routes =
    JSON.parse(
        localStorage.getItem("busgo_routes") || "[]"
    );

const selectedRoute =
    routes.find(function (route) {

        return String(route.routeId) ===
               String(routeId);

    });


/* ==================================================
   ELEMENTS
   ================================================== */

const bookingSummary =
    document.getElementById("bookingSummary");

const bookingForm =
    document.getElementById("bookingForm");

const bookingMessage =
    document.getElementById("bookingMessage");

const confirmBookingButton =
    document.getElementById("confirmBookingButton");


/* ==================================================
   LOGIN INFORMATION
   ================================================== */

const token =
    localStorage.getItem("busgo_token");

const loggedInUser =
    JSON.parse(
        localStorage.getItem("busgo_user") || "null"
    );


/* ==================================================
   CHECK LOGIN
   ================================================== */

if (!token || !loggedInUser) {

    bookingMessage.innerHTML = `

        <div class="alert alert-warning">

            <i class="bi bi-exclamation-triangle me-2"></i>

            Please login before making a booking.

        </div>

    `;

    confirmBookingButton.disabled = true;

}


/* ==================================================
   CHECK ROUTE AND SEAT
   ================================================== */

if (!selectedRoute || !selectedSeat) {

    bookingSummary.innerHTML = `

        <div class="alert alert-danger">

            <i class="bi bi-exclamation-circle me-2"></i>

            Booking information is incomplete.

            <br><br>

            Please go back and select a bus and seat again.

        </div>

    `;

    confirmBookingButton.disabled = true;

}


/* ==================================================
   DISPLAY BOOKING SUMMARY
   ================================================== */

if (selectedRoute && selectedSeat) {

    const bus =
        selectedRoute.bus;


    const departure =
        new Date(
            selectedRoute.departureTime
        ).toLocaleString();


    const arrival =
        new Date(
            selectedRoute.arrivalTime
        ).toLocaleString();


    bookingSummary.innerHTML = `

        <div class="mb-4">

            <small class="text-muted">
                Bus
            </small>

            <h5 class="fw-bold mb-1">

                ${bus.busName}

            </h5>

            <span class="text-muted">

                ${bus.operatorName}

            </span>

        </div>


        <div class="mb-3">

            <small class="text-muted">
                Bus Number
            </small>

            <div class="fw-semibold">

                ${bus.busNumber}

            </div>

        </div>


        <div class="mb-3">

            <small class="text-muted">
                Journey
            </small>

            <div class="fw-semibold">

                ${selectedRoute.source}

                <i class="bi bi-arrow-right text-primary mx-1"></i>

                ${selectedRoute.destination}

            </div>

        </div>


        <div class="mb-3">

            <small class="text-muted">
                Departure
            </small>

            <div class="fw-semibold">

                ${departure}

            </div>

        </div>


        <div class="mb-3">

            <small class="text-muted">
                Arrival
            </small>

            <div class="fw-semibold">

                ${arrival}

            </div>

        </div>


        <div class="mb-3">

            <small class="text-muted">
                Selected Seat
            </small>

            <div>

                <span class="badge bg-primary fs-6">

                    Seat ${selectedSeat.seatNumber}

                </span>

            </div>

        </div>


        <hr>


        <div class="d-flex justify-content-between align-items-center">

            <span class="fw-semibold">

                Total Fare

            </span>

            <span class="fs-4 fw-bold text-success">

                ₹${selectedRoute.fare}

            </span>

        </div>

    `;

}


/* ==================================================
   CREATE BOOKING
   ================================================== */

if (bookingForm) {

    bookingForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            /* ==========================================
               BASIC VALIDATION
               ========================================== */

            if (!token) {

                bookingMessage.innerHTML = `

                    <div class="alert alert-warning">

                        Please login before making a booking.

                    </div>

                `;

                return;

            }


            if (!selectedRoute) {

                bookingMessage.innerHTML = `

                    <div class="alert alert-danger">

                        Selected route information was not found.

                    </div>

                `;

                return;

            }


            if (!selectedSeat) {

                bookingMessage.innerHTML = `

                    <div class="alert alert-danger">

                        Selected seat information was not found.

                    </div>

                `;

                return;

            }


            /* ==========================================
               PASSENGER NAME
               ========================================== */

            const passengerName =
                document
                    .getElementById("passengerName")
                    .value
                    .trim();


            if (!passengerName) {

                bookingMessage.innerHTML = `

                    <div class="alert alert-danger">

                        Please enter the passenger name.

                    </div>

                `;

                return;

            }


            /* ==========================================
               BUTTON LOADING
               ========================================== */

            const originalButtonText =
                confirmBookingButton.innerHTML;


            confirmBookingButton.disabled = true;


            confirmBookingButton.innerHTML = `

                <span
                    class="spinner-border spinner-border-sm me-2">
                </span>

                Processing Booking...

            `;


            /* ==========================================
               BOOKING REQUEST
               ========================================== */

            try {

                console.log(
                    "Sending booking request..."
                );


                console.log(
                    "Route ID:",
                    selectedRoute.routeId
                );


                console.log(
                    "Seat ID:",
                    selectedSeat.seatId
                );


                console.log(
                    "Passenger:",
                    passengerName
                );


                const response =
                    await fetch(
                        `${API_BASE_URL}/bookings`,
                        {

                            method: "POST",

                            headers: {

                                "Content-Type":
                                    "application/json",

                                "Authorization":
                                    `Bearer ${token}`

                            },

                            body: JSON.stringify({

                                route: {

                                    routeId:
                                        selectedRoute.routeId

                                },

                                seat: {

                                    seatId:
                                        selectedSeat.seatId

                                },

                                passengerName:
                                    passengerName

                            })

                        }
                    );


                /* ==========================================
                   READ SERVER RESPONSE
                   ========================================== */

                const responseText =
                    await response.text();


                console.log(
                    "Booking response status:",
                    response.status
                );


                console.log(
                    "Booking response:",
                    responseText
                );


                /* ==========================================
                   HANDLE ERROR RESPONSE
                   ========================================== */

                if (!response.ok) {

                    throw new Error(
                        responseText ||
                        `Booking failed with status ${response.status}`
                    );

                }


                /* ==========================================
                   CONVERT RESPONSE TO JSON
                   ========================================== */

                let data;


                try {

                    data =
                        JSON.parse(responseText);

                }

                catch (jsonError) {

                    console.error(
                        "JSON parsing error:",
                        jsonError
                    );

                    throw new Error(
                        "Booking was processed, but the server returned an unexpected response."
                    );

                }


                console.log(
                    "Booking created successfully:",
                    data
                );


                /* ==========================================
                   SAVE BOOKING
                   ========================================== */

                localStorage.setItem(
                    "busgo_booking",
                    JSON.stringify(data)
                );


                /* ==========================================
                   REDIRECT TO SUCCESS PAGE
                   ========================================== */

                window.location.href =
                    "booking-success.html";

            }


            /* ==========================================
               FETCH / NETWORK ERROR
               ========================================== */

            catch (error) {

                console.error(
                    "Booking error:",
                    error
                );


                bookingMessage.innerHTML = `

                    <div class="alert alert-danger">

                        <strong>
                            Booking failed.
                        </strong>

                        <br><br>

                        ${error.message}

                        <hr>

                        <small class="text-muted">

                            Please check that the Spring Boot
                            backend is running on port 8080.

                        </small>

                    </div>

                `;


                confirmBookingButton.disabled =
                    false;


                confirmBookingButton.innerHTML =
                    originalButtonText;

            }

        }
    );

}