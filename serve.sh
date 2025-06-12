#!/bin/bash

# Advanced Clojure Katas Development Server
# Serves on 0.0.0.0 with health checks and auto-rebuild

set -e

PORT=${PORT:-8080}
HOST=${HOST:-0.0.0.0}

echo "🚀 Starting Clojure Katas Development Server"
echo "📍 Host: $HOST"
echo "🔌 Port: $PORT"

# Function to check if port is available
check_port() {
    if lsof -Pi :$PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "❌ Port $PORT is already in use"
        echo "🔍 Process using port:"
        lsof -Pi :$PORT -sTCP:LISTEN
        exit 1
    fi
}

# Function to build the project
build_project() {
    echo "🔨 Building ClojureScript..."
    if command -v npx &> /dev/null; then
        npx shadow-cljs release app
    else
        echo "❌ npx not found. Please install Node.js"
        exit 1
    fi
    echo "✅ Build complete"
}

# Function to start development server with auto-rebuild
start_dev_server() {
    echo "🔄 Starting development server with auto-rebuild..."
    npx shadow-cljs watch app &
    SHADOW_PID=$!
    
    # Wait for shadow-cljs to start
    sleep 5
    
    echo "📡 Starting HTTP server on $HOST:$PORT"
    cd public
    
    if command -v python3 &> /dev/null; then
        python3 -m http.server $PORT --bind $HOST &
        HTTP_PID=$!
    elif command -v python &> /dev/null; then
        python -m SimpleHTTPServer $PORT &
        HTTP_PID=$!
    else
        echo "❌ Python not found. Cannot start HTTP server"
        kill $SHADOW_PID 2>/dev/null || true
        exit 1
    fi
    
    echo "✅ Servers started!"
    echo "🌐 Access at: http://$HOST:$PORT"
    echo "🔗 Local: http://localhost:$PORT"
    echo "📊 Health check: http://$HOST:$PORT/health.json"
    
    # Cleanup function
    cleanup() {
        echo "🛑 Shutting down servers..."
        kill $SHADOW_PID 2>/dev/null || true
        kill $HTTP_PID 2>/dev/null || true
        exit 0
    }
    
    trap cleanup SIGINT SIGTERM
    
    # Wait for both processes
    wait $SHADOW_PID $HTTP_PID
}

# Function to start production server
start_prod_server() {
    echo "🏭 Starting production server..."
    build_project
    
    cd public
    echo "📡 Starting HTTP server on $HOST:$PORT"
    
    if command -v python3 &> /dev/null; then
        python3 -m http.server $PORT --bind $HOST
    elif command -v python &> /dev/null; then
        python -m SimpleHTTPServer $PORT
    else
        echo "❌ Python not found. Cannot start HTTP server"
        exit 1
    fi
}

# Function to run health checks
run_health_check() {
    echo "🏥 Running health checks..."
    
    # Check if server is running
    if curl -f -s "http://$HOST:$PORT/health.json" > /dev/null; then
        echo "✅ Health endpoint accessible"
    else
        echo "❌ Health endpoint not accessible"
        return 1
    fi
    
    # Check main page
    if curl -f -s "http://$HOST:$PORT/" | grep -q "Clojure Katas"; then
        echo "✅ Main page loads correctly"
    else
        echo "❌ Main page not loading correctly"
        return 1
    fi
    
    # Check JavaScript bundle
    if curl -f -s -I "http://$HOST:$PORT/js/main.js" > /dev/null; then
        echo "✅ JavaScript bundle accessible"
    else
        echo "❌ JavaScript bundle not accessible"
        return 1
    fi
    
    echo "🎉 All health checks passed!"
}

# Function to show network information
show_network_info() {
    echo "🌐 Network Information:"
    echo "   Local: http://localhost:$PORT"
    echo "   Host: http://$HOST:$PORT"
    
    # Show all network interfaces
    echo "📡 Available network interfaces:"
    if command -v ip &> /dev/null; then
        ip addr show | grep "inet " | grep -v "127.0.0.1" | awk '{print "   " $2}'
    elif command -v ifconfig &> /dev/null; then
        ifconfig | grep "inet " | grep -v "127.0.0.1" | awk '{print "   " $2}'
    fi
    
    echo "🔗 Curl validation commands:"
    echo "   curl http://$HOST:$PORT/"
    echo "   curl http://$HOST:$PORT/health.json"
}

# Main execution
case "${1:-dev}" in
    "dev"|"development")
        check_port
        show_network_info
        start_dev_server
        ;;
    "prod"|"production")
        check_port
        show_network_info
        start_prod_server
        ;;
    "build")
        build_project
        ;;
    "health")
        run_health_check
        ;;
    "network")
        show_network_info
        ;;
    *)
        echo "Usage: $0 {dev|prod|build|health|network}"
        echo ""
        echo "Commands:"
        echo "  dev        Start development server with auto-rebuild"
        echo "  prod       Start production server"
        echo "  build      Build project only"
        echo "  health     Run health checks"
        echo "  network    Show network information"
        echo ""
        echo "Environment variables:"
        echo "  PORT       Server port (default: 8080)"
        echo "  HOST       Server host (default: 0.0.0.0)"
        exit 1
        ;;
esac