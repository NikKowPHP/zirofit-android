src/app/api/auth/refresh/route.ts

import { NextRequest } from "next/server";
import { z } from "zod";
import { createClient } from "@/lib/supabase/server";
import {
  jsonSuccess,
  jsonError,
  handleRouteError,
  ApiError,
} from "@/lib/api/response";
import { devLog } from "@/lib/dev-logger";

const refreshSchema = z.object({
  refreshToken: z.string().min(1, "Refresh token is required"),
});

export async function POST(request: NextRequest) {
  try {
    const payload = await request.json().catch(() => {
      throw new ApiError(400, "Invalid JSON body.");
    });

    // 1. Validate Input
    const parsed = refreshSchema.safeParse(payload);
    if (!parsed.success) {
      throw new ApiError(422, "Validation failed.", {
        details: parsed.error.format(),
      });
    }

    const { refreshToken } = parsed.data;
    devLog("Attempting session refresh via API");

    // 2. Initialize Supabase
    const supabase = await createClient();

    // 3. Refresh the Session
    const { data, error } = await supabase.auth.refreshSession({
      refresh_token: refreshToken,
    });

    if (error || !data.session) {
      devLog("Refresh failed", error?.message);
      // Return 401 so the mobile app knows to force a logout/re-login
      return jsonError(error?.message || "Invalid or expired refresh token", {
        status: 401,
        code: "auth_invalid_refresh_token",
      });
    }

    devLog("Session refreshed successfully", { userId: data.session.user.id });

    // 4. Return standard project response
    return jsonSuccess({
      accessToken: data.session.access_token,
      refreshToken: data.session.refresh_token,
      expiresAt: data.session.expires_at, // Helpful for mobile to know when to refresh next
      user: data.session.user,
    });
  } catch (error) {
    // This will log 500s to your SystemError table automatically
    return handleRouteError(error);
  }
}
