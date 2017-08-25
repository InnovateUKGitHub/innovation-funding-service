environment=$1

# Get the deviations to apply to the random timers on test steps.  For the Performance environment(s), we want to remove deviation as much as possible to
# allow us to better perform side-by-side analysis of performance on different branches
function test_deviations() {
    if [[ "${environment}" == *"perf"* ]]; then
        echo "-Jmicro_pause_deviation=0 -Jshort_pause_deviation=0 -Jnormal_pause_deviation=0 -Jlong_pause_deviation=0 -Jlonger_pause_deviation=0"
    else
        echo "-Jmicro_pause_deviation=100 -Jshort_pause_deviation=500 -Jnormal_pause_deviation=5000 -Jlong_pause_deviation=10000 -Jlonger_pause_deviation=15000"
    fi
}

# run the tests against the given environment
function perform_tests() {

    rm -f /tmp/${environment}-run.jtl
    jmeter -n -l /tmp/${environment}-run.jtl -Jinvite_user=0 -Japplicant_users=600 -Jregistration_users=0 $(test_deviations) -p${environment}.properties -tload_test_representative.jmx
}

# the main entry point into this script
function run() {
    echo "This will run the performance tests against ${environment}.  In order to run this, you should ensure that a fresh set of webtest data is available on ${environment} (although these tests themselves are rerunnable on a fresh set)."
    echo ""

    if [[ "${environment}" != *"local"* ]]; then

        echo "You should also ensure that the environment is free to use for this purpose.  Please check the #environment-updates channel to see if it reserved at this time."
        echo ""

        read -r -p "Are you happy to run these tests at this time? [y/N]" response

        if [[ $response =~ ^([yY][eE][sS]|[yY])$ ]]; then
            perform_tests
        else
            echo "No problem.  See you next time!"
        fi

    else
        perform_tests
    fi
}

run