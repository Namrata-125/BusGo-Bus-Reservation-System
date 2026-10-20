
const API_BASE_URL = "http://localhost:8080/api";

const token = localStorage.getItem("busgo_token");

const loading = document.getElementById("loading");
const emptyState = document.getElementById("emptyState");
const bookingsContainer = document.getElementById("bookingsContainer");
const message = document.getElementById("message");
const logoutBtn = document.getElementById("logoutBtn");

// Logout
if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
        localStorage.removeItem("busgo_token");
        localStorage.removeItem("busgo_user");
        window.location.href = "login.html";
    });
}

// Display messages
function showMessage(text, type = "danger") {
    if (!message) return;

    message.textContent = text;
    message.className = "alert alert-" + type;
}

// Format date and time
function formatDateTime(value) {
    if (!value) return "Not available";

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return "Not available";
    }

    return date.toLocaleString("en-IN", {
        dateStyle: "medium",
        timeStyle: "short"
    });
}

// Format amount
function formatAmount(value) {
    return Number(value || 0).toLocaleString("en-IN", {
        style: "currency",
        currency: "INR"
    });
}

// Get payment badge and status information
function getPaymentInfo(status) {
    if (!status) {
        return {
            badgeClass: "bg-secondary",
            text: "UNPAID",
            label: "Unpaid"
        };
    }

    switch (status) {
        case "REFUNDED":
            return {
                badgeClass: "bg-info text-dark",
                text: "REFUNDED (SIMULATED)",
                label: "Refunded (Simulated)"
            };
        case "REFUND_PENDING":
            return {
                badgeClass: "bg-warning text-dark",
                text: "REFUND PENDING (SIMULATED)",
                label: "Refund Pending"
            };
        case "SUCCESS":
            return {
                badgeClass: "bg-success",
                text: "PAID",
                label: "Paid"
            };
        case "FAILED":
            return {
                badgeClass: "bg-danger",
                text: "FAILED",
                label: "Failed"
            };
        case "PENDING":
            return {
                badgeClass: "bg-secondary",
                text: "PENDING",
                label: "Pending"
            };
        default:
            return {
                badgeClass: "bg-secondary",
                text: status,
                label: status
            };
    }
}

// Create an information field
function createInfoField(label, value, columnClass = "col-md-3") {
    const column = document.createElement("div");
    column.className = columnClass;

    const labelElement = document.createElement("small");
    labelElement.className = "text-muted";
    labelElement.textContent = label;

    const valueElement = document.createElement("div");
    valueElement.className = "fw-semibold";
    valueElement.textContent = value || "—";

    column.appendChild(labelElement);
    column.appendChild(valueElement);

    return column;
}

// Create a detail paragraph
function createDetailParagraph(label, value) {
    const paragraph = document.createElement("p");
    paragraph.className = "mb-2";

    const strong = document.createElement("strong");
    strong.textContent = label + " ";

    const text = document.createElement("span");
    text.textContent = value || "—";

    paragraph.appendChild(strong);
    paragraph.appendChild(text);

    return paragraph;
}

// Create booking card
function createBookingCard(booking) {
    const route = booking.route || {};
    const bus = route.bus || {};
    const seat = booking.seat || {};

    const isCancelled = booking.bookingStatus === "CANCELLED";

    const payments = Array.isArray(booking.payments) ? booking.payments : [];
    const payment = payments.length > 0 ? payments[payments.length - 1] : null;
    const paymentInfo = getPaymentInfo(payment ? payment.paymentStatus : null);
    const isRefunded = payment && payment.paymentStatus === "REFUNDED";

    const card = document.createElement("div");
    card.className = "col-12";

    const cardBody = document.createElement("div");
    cardBody.className = "card shadow-sm border-0 rounded-4";

    const body = document.createElement("div");
    body.className = "card-body p-4";

    // Heading
    const heading = document.createElement("div");
    heading.className =
        "d-flex flex-wrap justify-content-between align-items-start gap-3 mb-3";

    const headingInfo = document.createElement("div");

    const bookingId = document.createElement("small");
    bookingId.className = "text-muted";
    bookingId.textContent = "Booking ID: #" + booking.bookingId;

    const busName = document.createElement("h5");
    busName.className = "fw-bold mt-1 mb-1";
    busName.textContent = bus.busName || "Bus";

    const routeName = document.createElement("p");
    routeName.className = "text-muted mb-0";
    routeName.textContent =
        (route.source || "Unknown") + " → " +
        (route.destination || "Unknown");

    headingInfo.appendChild(bookingId);
    headingInfo.appendChild(busName);
    headingInfo.appendChild(routeName);

    const badgeWrapper = document.createElement("div");
    badgeWrapper.className = "d-flex flex-wrap gap-2 align-items-center";

    const statusBadge = document.createElement("span");
    statusBadge.className =
        "badge " + (isCancelled ? "bg-danger" : "bg-success");
    statusBadge.textContent = booking.bookingStatus || "UNKNOWN";

    const paymentBadge = document.createElement("span");
    paymentBadge.className = "badge " + paymentInfo.badgeClass;
    paymentBadge.textContent = paymentInfo.text;

    badgeWrapper.appendChild(statusBadge);
    badgeWrapper.appendChild(paymentBadge);

    heading.appendChild(headingInfo);
    heading.appendChild(badgeWrapper);

    // Booking summary
    const infoRow = document.createElement("div");
    infoRow.className = "row g-3";

    infoRow.appendChild(
        createInfoField("Bus Number", bus.busNumber)
    );

    infoRow.appendChild(
        createInfoField(
            "Departure",
            formatDateTime(route.departureTime)
        )
    );

    infoRow.appendChild(
        createInfoField("Seat", seat.seatNumber, "col-md-2")
    );

    infoRow.appendChild(
        createInfoField(
            "Amount",
            formatAmount(booking.totalAmount),
            "col-md-2"
        )
    );

    infoRow.appendChild(
        createInfoField(
            "Passenger",
            booking.passengerName,
            "col-md-2"
        )
    );

    // Buttons
    const divider = document.createElement("hr");

    const actions = document.createElement("div");
    actions.className = "d-flex flex-wrap gap-2";

    const viewButton = document.createElement("button");
    viewButton.type = "button";
    viewButton.className = "btn btn-outline-primary btn-sm";
    viewButton.textContent = "View Details";

    actions.appendChild(viewButton);

    let cancelButton = null;

    if (!isCancelled) {
        cancelButton = document.createElement("button");
        cancelButton.type = "button";
        cancelButton.className = "btn btn-outline-danger btn-sm";
        cancelButton.textContent = "Cancel Booking";

        actions.appendChild(cancelButton);
    }

    // Additional details
    const details = document.createElement("div");
    details.className = "booking-details d-none mt-3 p-3 bg-light rounded-3";

    details.appendChild(
        createDetailParagraph(
            "Arrival:",
            formatDateTime(route.arrivalTime)
        )
    );

    details.appendChild(
        createDetailParagraph(
            "Bus Type:",
            bus.busType || "Not specified"
        )
    );

    details.appendChild(
        createDetailParagraph(
            "Operator:",
            bus.operatorName || "Not specified"
        )
    );

    details.appendChild(
        createDetailParagraph(
            "Booking Date:",
            formatDateTime(booking.bookingDate)
        )
    );

    if (payment) {
        details.appendChild(
            createDetailParagraph(
                "Payment Method:",
                payment.paymentMethod || "Not specified"
            )
        );

        details.appendChild(
            createDetailParagraph(
                "Transaction ID:",
                payment.transactionId || "Not specified"
            )
        );

        details.appendChild(
            createDetailParagraph(
                "Payment Status:",
                payment.paymentStatus || "Not specified"
            )
        );

        details.appendChild(
            createDetailParagraph(
                "Payment Date:",
                formatDateTime(payment.paymentDate)
            )
        );
    }

    // Assemble card
    body.appendChild(heading);
    body.appendChild(infoRow);

    if (isRefunded) {
        const refundNotice = document.createElement("div");
        refundNotice.className = "alert alert-info py-2 px-3 mt-3 mb-0 small";
        refundNotice.innerHTML =
            "<strong>Refund Status: REFUNDED (SIMULATED)</strong><br>" +
            "<span>This refund is simulated for demonstration purposes. No real money has been transferred.</span>";
        body.appendChild(refundNotice);
    }

    body.appendChild(divider);
    body.appendChild(actions);
    body.appendChild(details);

    cardBody.appendChild(body);
    card.appendChild(cardBody);

    // View details
    viewButton.addEventListener("click", function () {
        details.classList.toggle("d-none");

        viewButton.textContent = details.classList.contains("d-none")
            ? "View Details"
            : "Hide Details";
    });

    // Cancel booking
    if (cancelButton) {
        cancelButton.addEventListener("click", async function () {
            const confirmed = window.confirm(
                "Are you sure you want to cancel booking #" +
                booking.bookingId + "?"
            );

            if (!confirmed) return;

            cancelButton.disabled = true;
            cancelButton.textContent = "Cancelling...";

            try {
                const response = await fetch(
                    API_BASE_URL + "/bookings/" +
                    booking.bookingId + "/cancel",
                    {
                        method: "PUT",
                        headers: {
                            Authorization: "Bearer " + token
                        }
                    }
                );

                const responseText = await response.text();

                if (!response.ok) {
                    throw new Error(
                        responseText || "Unable to cancel booking."
                    );
                }

                showMessage(
                    "Booking #" + booking.bookingId +
                    " cancelled successfully.",
                    "success"
                );

                await loadBookings();

            } catch (error) {
                showMessage(
                    error.message || "Unable to cancel booking."
                );

                cancelButton.disabled = false;
                cancelButton.textContent = "Cancel Booking";
            }
        });
    }

    return card;
}

// Load booking history
async function loadBookings() {
    if (!loading || !emptyState || !bookingsContainer || !message) {
        console.error(
            "My Bookings page is missing a required HTML element."
        );
        return;
    }

    loading.classList.remove("d-none");
    emptyState.classList.add("d-none");
    bookingsContainer.replaceChildren();
    message.classList.add("d-none");

    try {
        const response = await fetch(
            API_BASE_URL + "/bookings/my",
            {
                method: "GET",
                headers: {
                    Authorization: "Bearer " + token
                }
            }
        );

        const responseText = await response.text();

        if (!response.ok) {
            throw new Error(
                responseText || "Unable to load your bookings."
            );
        }

        const bookings = JSON.parse(responseText);

        if (!Array.isArray(bookings) || bookings.length === 0) {
            emptyState.classList.remove("d-none");
            return;
        }

        // Concurrently fetch payment records for each booking
        await Promise.all(
            bookings.map(async function (booking) {
                try {
                    const payResponse = await fetch(
                        API_BASE_URL + "/payments/booking/" + booking.bookingId,
                        {
                            method: "GET",
                            headers: {
                                Authorization: "Bearer " + token
                            }
                        }
                    );

                    if (payResponse.ok) {
                        const payText = await payResponse.text();
                        booking.payments = payText ? JSON.parse(payText) : [];
                    } else {
                        booking.payments = [];
                    }
                } catch (err) {
                    console.error(
                        "Error fetching payments for booking #" + booking.bookingId,
                        err
                    );
                    booking.payments = [];
                }
            })
        );

        bookings.forEach(function (booking) {
            bookingsContainer.appendChild(
                createBookingCard(booking)
            );
        });

    } catch (error) {
        console.error("My Bookings error:", error);

        showMessage(
            error.message || "Could not load bookings. Please try again."
        );

    } finally {
        loading.classList.add("d-none");
    }
}

// Start page
if (!token) {
    window.location.href = "login.html";
} else {
    loadBookings();
}

