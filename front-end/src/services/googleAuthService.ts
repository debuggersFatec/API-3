export async function postGoogleToken(userId: string, token: string) {
    const resp = await fetch(`/api/google/token`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userId, token }),
    });
    return resp.json();
}

export async function fetchGoogleEvents(userId: string, timeMin?: string, timeMax?: string) {
    const params = new URLSearchParams();
    params.set("userId", userId);
    if (timeMin) params.set("timeMin", timeMin);
    if (timeMax) params.set("timeMax", timeMax);
    const resp = await fetch(`/api/google/events?${params.toString()}`);
    return resp.json();
}



