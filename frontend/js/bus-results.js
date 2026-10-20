/* ================= BUS RESULTS ================= */

const resultsContainer =
    document.getElementById("busResults");

const searchSummary =
    document.getElementById("searchSummary");


const source =
    localStorage.getItem("busgo_source");

const destination =
    localStorage.getItem("busgo_destination");

const journeyDate =
    localStorage.getItem("busgo_date");

const routes =
    JSON.parse(
        localStorage.getItem("busgo_routes") || "[]"
    );


/* ================= SEARCH SUMMARY ================= */

if (source && destination && journeyDate) {

    searchSummary.textContent =
        `${source} → ${destination} | ${journeyDate}`;

}


/* ================= NO RESULTS ================= */

if (routes.length === 0) {

    resultsContainer.innerHTML = `

        <div class="col-12">

            <div class="alert alert-warning text-center">

                <i class="bi bi-exclamation-circle me-2"></i>

                No buses found for this journey.

            </div>

        </div>

    `;

}


/* ================= DISPLAY RESULTS ================= */

else {

    routes.forEach(function (route) {

        const bus =
            route.bus;


        const departure =
            new Date(route.departureTime)
                .toLocaleTimeString(
                    [],
                    {
                        hour: "2-digit",
                        minute: "2-digit"
                    }
                );


        const arrival =
            new Date(route.arrivalTime)
                .toLocaleTimeString(
                    [],
                    {
                        hour: "2-digit",
                        minute: "2-digit"
                    }
                );


        resultsContainer.innerHTML += `

            <div class="col-lg-6">

                <div class="card border-0 shadow-sm h-100">

                    <div class="card-body p-4">


                        <div class="d-flex justify-content-between align-items-start">


                            <div>

                                <h4 class="fw-bold mb-1">

                                    ${bus.busName}

                                </h4>

                                <p class="text-muted mb-0">

                                    ${bus.operatorName}

                                </p>

                            </div>


                            <span class="badge bg-primary">

                                ${bus.busType}

                            </span>


                        </div>


                        <hr>


                        <div class="row text-center">


                            <div class="col-4">

                                <h5 class="fw-bold">

                                    ${departure}

                                </h5>

                                <small class="text-muted">

                                    ${route.source}

                                </small>

                            </div>


                            <div class="col-4">

                                <i class="bi bi-arrow-right fs-4 text-primary"></i>

                            </div>


                            <div class="col-4">

                                <h5 class="fw-bold">

                                    ${arrival}

                                </h5>

                                <small class="text-muted">

                                    ${route.destination}

                                </small>

                            </div>

                        </div>


                        <hr>


                        <div class="d-flex justify-content-between align-items-center">


                            <div>

                                <small class="text-muted">

                                    Bus Number

                                </small>

                                <div class="fw-semibold">

                                    ${bus.busNumber}

                                </div>

                            </div>


                            <div>

                                <small class="text-muted">

                                    Fare

                                </small>

                                <div class="fw-bold text-success">

                                    ₹${route.fare}

                                </div>

                            </div>


                            <button
                                class="btn btn-primary"
                                onclick="selectBus(${route.routeId})">

                                Select Bus

                            </button>


                        </div>


                    </div>

                </div>

            </div>

        `;

    });

}


/* ================= SELECT BUS ================= */

function selectBus(routeId) {

    localStorage.setItem(
        "busgo_selected_route",
        routeId
    );


    window.location.href =
        "seat-selection.html";

}