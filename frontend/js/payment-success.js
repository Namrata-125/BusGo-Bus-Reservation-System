/* ==================================================
   GET SAVED PAYMENT
   ================================================== */

const savedPayment =
    JSON.parse(
        localStorage.getItem("busgo_payment") || "null"
    );


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

const transactionInfo =
    document.getElementById("transactionInfo");

const paymentDetails =
    document.getElementById("paymentDetails");


/* ==================================================
   CHECK PAYMENT
   ================================================== */

if (!savedPayment) {

    transactionInfo.innerHTML = `

        <div class="alert alert-warning">

            Payment information could not be found.

        </div>

    `;

    paymentDetails.innerHTML = `

        <div class="text-center">

            <p class="text-muted">

                Please check your booking and payment history.

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
       TRANSACTION ID
       ================================================== */

    transactionInfo.innerHTML = `

        <span
            class="badge bg-success fs-6 px-3 py-2">

            <i class="bi bi-check-circle"></i>

            Transaction:
            ${savedPayment.transactionId}

        </span>

    `;


    /* ==================================================
       DISPLAY PAYMENT DETAILS
       ================================================== */

    paymentDetails.innerHTML = `

        <div class="row g-4">


            <!-- BOOKING ID -->

            <div class="col-md-6">

                <small class="text-muted">

                    Booking ID

                </small>

                <div class="fw-semibold">

                    #${savedPayment.booking.bookingId}

                </div>

            </div>


            <!-- PAYMENT ID -->

            <div class="col-md-6">

                <small class="text-muted">

                    Payment ID

                </small>

                <div class="fw-semibold">

                    #${savedPayment.paymentId}

                </div>

            </div>


            <!-- PAYMENT METHOD -->

            <div class="col-md-6">

                <small class="text-muted">

                    Payment Method

                </small>

                <div class="fw-semibold">

                    ${savedPayment.paymentMethod}

                </div>

            </div>


            <!-- PAYMENT STATUS -->

            <div class="col-md-6">

                <small class="text-muted">

                    Payment Status

                </small>

                <div>

                    <span class="badge bg-success">

                        ${savedPayment.paymentStatus}

                    </span>

                </div>

            </div>


            <!-- TRANSACTION -->

            <div class="col-md-6">

                <small class="text-muted">

                    Transaction ID

                </small>

                <div class="fw-semibold text-break">

                    ${savedPayment.transactionId}

                </div>

            </div>


            <!-- PAYMENT DATE -->

            <div class="col-md-6">

                <small class="text-muted">

                    Payment Date

                </small>

                <div class="fw-semibold">

                    ${new Date(
                        savedPayment.paymentDate
                    ).toLocaleString()}

                </div>

            </div>

        </div>


        <hr class="my-4">


        <!-- AMOUNT -->

        <div
            class="d-flex justify-content-between align-items-center">

            <span class="fw-semibold">

                Amount Paid

            </span>

            <span
                class="fs-3 fw-bold text-success">

                ₹${savedPayment.amount}

            </span>

        </div>

    `;

}