const API_BASE_URL = "http://localhost:8080/api";

/* ==================================================
   GET BOOKING
   ================================================== */

const savedBooking =
    JSON.parse(
        localStorage.getItem("busgo_booking") || "null"
    );


/* ==================================================
   GET AUTHENTICATION
   ================================================== */

const token =
    localStorage.getItem("busgo_token");


/* ==================================================
   ELEMENTS
   ================================================== */

const paymentSummary =
    document.getElementById("paymentSummary");

const paymentForm =
    document.getElementById("paymentForm");

const paymentMessage =
    document.getElementById("paymentMessage");

const payButton =
    document.getElementById("payButton");


/* ==================================================
   CHECK BOOKING
   ================================================== */

if (!savedBooking) {

    paymentSummary.innerHTML = `

        <div class="alert alert-danger">

            Booking information was not found.

            <br><br>

            Please complete a booking first.

        </div>

    `;

    payButton.disabled = true;

}


/* ==================================================
   CHECK LOGIN
   ================================================== */

if (!token) {

    paymentMessage.innerHTML = `

        <div class="alert alert-warning">

            Please login before making a payment.

        </div>

    `;

    payButton.disabled = true;

}


/* ==================================================
   DISPLAY BOOKING SUMMARY
   ================================================== */

if (savedBooking) {

    const bus =
        savedBooking.route.bus;


    paymentSummary.innerHTML = `

        <div class="mb-4">

            <small class="text-muted">
                Booking ID
            </small>

            <h5 class="fw-bold">

                #${savedBooking.bookingId}

            </h5>

        </div>


        <div class="mb-3">

            <small class="text-muted">
                Passenger
            </small>

            <div class="fw-semibold">

                ${savedBooking.passengerName}

            </div>

        </div>


        <div class="mb-3">

            <small class="text-muted">
                Bus
            </small>

            <div class="fw-semibold">

                ${bus.busName}

            </div>

        </div>


        <div class="mb-3">

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


        <div class="mb-3">

            <small class="text-muted">
                Seat
            </small>

            <div>

                <span class="badge bg-primary">

                    Seat ${savedBooking.seat.seatNumber}

                </span>

            </div>

        </div>


        <hr>


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

    `;

}


/* ==================================================
   PAYMENT FORM
   ================================================== */

paymentForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        /* ==========================================
           VALIDATION
           ========================================== */

        if (!savedBooking) {

            return;

        }


        if (!token) {

            return;

        }


        /* ==========================================
           GET PAYMENT METHOD
           ========================================== */

        const selectedMethod =
            document.querySelector(
                'input[name="paymentMethod"]:checked'
            );


        if (!selectedMethod) {

            paymentMessage.innerHTML = `

                <div class="alert alert-warning">

                    Please select a payment method.

                </div>

            `;

            return;

        }


        const paymentMethod =
            selectedMethod.value;


        /* ==========================================
           BUTTON LOADING
           ========================================== */

        const originalButtonText =
            payButton.innerHTML;


        payButton.disabled = true;


        payButton.innerHTML = `

            <span
                class="spinner-border spinner-border-sm me-2">
            </span>

            Processing Payment...

        `;


        paymentMessage.innerHTML = "";


        /* ==========================================
           PAYMENT REQUEST
           ========================================== */

        try {

            console.log(
                "Processing payment..."
            );


            const response =
                await fetch(
                    `${API_BASE_URL}/payments`,
                    {

                        method: "POST",

                        headers: {

                            "Content-Type":
                                "application/json",

                            "Authorization":
                                `Bearer ${token}`

                        },

                        body: JSON.stringify({

                            booking: {

                                bookingId:
                                    savedBooking.bookingId

                            },

                            paymentMethod:
                                paymentMethod

                        })

                    }
                );


            /* ==========================================
               READ RESPONSE
               ========================================== */

            const responseText =
                await response.text();


            console.log(
                "Payment response status:",
                response.status
            );


            console.log(
                "Payment response:",
                responseText
            );


            /* ==========================================
               HANDLE ERROR
               ========================================== */

            if (!response.ok) {

                throw new Error(
                    responseText ||
                    `Payment failed with status ${response.status}`
                );

            }


            /* ==========================================
               PARSE PAYMENT
               ========================================== */

            const payment =
                JSON.parse(responseText);


            console.log(
                "Payment successful:",
                payment
            );


            /* ==========================================
               SAVE PAYMENT
               ========================================== */

            localStorage.setItem(
                "busgo_payment",
                JSON.stringify(payment)
            );


            /* ==========================================
               REDIRECT
               ========================================== */

            window.location.href =
                "payment-success.html";

        }


        /* ==========================================
           ERROR
           ========================================== */

        catch (error) {

            console.error(
                "Payment error:",
                error
            );


            paymentMessage.innerHTML = `

                <div class="alert alert-danger">

                    <strong>
                        Payment failed.
                    </strong>

                    <br><br>

                    ${error.message}

                </div>

            `;


            payButton.disabled = false;


            payButton.innerHTML =
                originalButtonText;

        }

    }
);