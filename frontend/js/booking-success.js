/* ==================================================
   GET SAVED BOOKING
   ================================================== */

const savedBooking =
    JSON.parse(
        localStorage.getItem("busgo_booking") || "null"
    );


/* ==================================================
   ELEMENTS
   ================================================== */

const bookingIdElement =
    document.getElementById("bookingId");

const bookingDetails =
    document.getElementById("bookingDetails");


/* ==================================================
   CHECK BOOKING
   ================================================== */

if (!savedBooking) {

    bookingIdElement.innerHTML = `

        <div class="alert alert-warning">

            Booking information could not be found.

        </div>

    `;

    bookingDetails.innerHTML = `

        <div class="text-center">

            <p class="text-muted">

                Please check your booking history.

            </p>

            <a
                href="index.html"
                class="btn btn-primary">

                Go to Home

            </a>

        </div>

    `;

} else {


    /* ==================================================
       BOOKING ID
       ================================================== */

    bookingIdElement.innerHTML = `

        <span class="badge bg-success fs-6 px-3 py-2">

            Booking ID: #${savedBooking.bookingId}

        </span>

    `;


    /* ==================================================
       GET BUS INFORMATION
       ================================================== */

    const bus =
        savedBooking.route.bus;


    /* ==================================================
       DATE / TIME
       ================================================== */

    const departure =
        new Date(
            savedBooking.route.departureTime
        ).toLocaleString();


    const arrival =
        new Date(
            savedBooking.route.arrivalTime
        ).toLocaleString();


    /* ==================================================
       DISPLAY DETAILS
       ================================================== */

    bookingDetails.innerHTML = `

        <div class="row g-4">


            <!-- BUS -->

            <div class="col-md-6">

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


            <!-- BUS NUMBER -->

            <div class="col-md-6">

                <small class="text-muted">

                    Bus Number

                </small>

                <div class="fw-semibold">

                    ${bus.busNumber}

                </div>

            </div>


            <!-- PASSENGER -->

            <div class="col-md-6">

                <small class="text-muted">

                    Passenger Name

                </small>

                <div class="fw-semibold">

                    ${savedBooking.passengerName}

                </div>

            </div>


            <!-- SEAT -->

            <div class="col-md-6">

                <small class="text-muted">

                    Seat Number

                </small>

                <div>

                    <span class="badge bg-primary fs-6">

                        Seat
                        ${savedBooking.seat.seatNumber}

                    </span>

                </div>

            </div>


            <!-- JOURNEY -->

            <div class="col-md-6">

                <small class="text-muted">

                    Journey

                </small>

                <div class="fw-semibold">

                    ${savedBooking.route.source}

                    <i
                        class="bi bi-arrow-right text-primary mx-1">
                    </i>

                    ${savedBooking.route.destination}

                </div>

            </div>


            <!-- BUS TYPE -->

            <div class="col-md-6">

                <small class="text-muted">

                    Bus Type

                </small>

                <div class="fw-semibold">

                    ${bus.busType}

                </div>

            </div>


            <!-- DEPARTURE -->

            <div class="col-md-6">

                <small class="text-muted">

                    Departure

                </small>

                <div class="fw-semibold">

                    ${departure}

                </div>

            </div>


            <!-- ARRIVAL -->

            <div class="col-md-6">

                <small class="text-muted">

                    Arrival

                </small>

                <div class="fw-semibold">

                    ${arrival}

                </div>

            </div>

        </div>


        <hr class="my-4">


        <!-- TOTAL -->

        <div
            class="d-flex justify-content-between align-items-center">

            <span class="fw-semibold">

                Total Amount

            </span>

            <span
                class="fs-3 fw-bold text-success">

                ₹${savedBooking.totalAmount}

            </span>

        </div>


        <div class="mt-4">

            <span
                class="badge bg-success">

                <i class="bi bi-check-circle"></i>

                ${savedBooking.bookingStatus}

            </span>

        </div>

    `;

}