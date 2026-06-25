import React, { useState, useEffect, useRef, useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import MainLayout from "../../components/layout/MainLayout";
import "./AuthPages.css";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

const LoginPage = () => {
  const navigate = useNavigate();
  const { login, loginGoogle } = useAuth(); // Giả định loginGoogle đã có trong AuthContext

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // Ref để Google nhúng nút đăng nhập chuẩn vào
  const googleButtonRef = useRef(null);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError("");
  };

  // Hàm xử lý sau khi Google trả về token
  const handleGoogleResponse = useCallback(
    async (response) => {
      try {
        setLoading(true);
        setError("");
        // Gửi idToken (response.credential) xuống Backend
        const data = await loginGoogle(response.credential);

        // Chuyển hướng theo Role
        if (data.role === "ADMIN") {
          navigate("/admin");
        } else if (data.role === "PT") {
          navigate("/pt/dashboard");
        } else {
          navigate("/member/dashboard");
        }
      } catch (err) {
        const resData = err.response?.data;
        setError(
          resData?.message ||
            "Đăng nhập bằng Google thất bại. Vui lòng thử lại!",
        );
      } finally {
        setLoading(false);
      }
    },
    [loginGoogle, navigate],
  );

  // Khởi tạo Google Sign-In Script và Render Button
  useEffect(() => {
    const loadGoogleScript = () => {
      if (window.google) {
        window.google.accounts.id.initialize({
          client_id: GOOGLE_CLIENT_ID,
          callback: handleGoogleResponse,
        });

        // Vẽ nút Google chuẩn vào thẻ div có ref = googleButtonRef
        window.google.accounts.id.renderButton(googleButtonRef.current, {
          theme: "outline",
          size: "large",
          width: "100%",
          text: "signin_with",
        });
      }
    };

    // Kiểm tra xem script đã tồn tại chưa để tránh add nhiều lần
    if (
      document.querySelector(
        "script[src='https://accounts.google.com/gsi/client']",
      )
    ) {
      loadGoogleScript();
    } else {
      const script = document.createElement("script");
      script.src = "https://accounts.google.com/gsi/client";
      script.async = true;
      script.defer = true;
      script.onload = loadGoogleScript;
      document.body.appendChild(script);
    }
  }, [handleGoogleResponse]);

  // Xử lý đăng nhập bằng Mật khẩu (Local)
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const data = await login(formData.email, formData.password);

      // Chuyển hướng theo Role
      if (data.role === "ADMIN") {
        navigate("/admin");
      } else if (data.role === "PT") {
        navigate("/pt/dashboard");
      } else {
        navigate("/member/dashboard");
      }
    } catch (err) {
      const resData = err.response?.data;
      setError(resData?.message || "Email hoặc mật khẩu không chính xác!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <MainLayout>
      <div className="auth-container">
        <div className="auth-card">
          <h2>ĐĂNG NHẬP</h2>

          {/* Hiển thị lỗi nếu có */}
          {error && (
            <div
              className="auth-error"
              style={{
                color: "red",
                marginBottom: "10px",
                textAlign: "center",
              }}
            >
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                name="email"
                placeholder="example@gmail.com"
                value={formData.email}
                onChange={handleChange}
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Mật khẩu</label>
              <input
                id="password"
                type="password"
                name="password"
                placeholder="Nhập mật khẩu"
                value={formData.password}
                onChange={handleChange}
                required
                disabled={loading}
              />
            </div>

            <button
              type="submit"
              className="btn-auth-submit"
              disabled={loading}
            >
              {loading ? "Đang xử lý..." : "ĐĂNG NHẬP"}
            </button>
          </form>

          <div
            className="auth-divider"
            style={{ textAlign: "center", margin: "20px 0" }}
          >
            hoặc
          </div>

          {/* Vùng chứa nút Đăng nhập Google chuẩn */}
          <div
            ref={googleButtonRef}
            className="google-button-wrapper"
            style={{
              display: "flex",
              justifyContent: "center",
              marginBottom: "15px",
            }}
          ></div>

          <div className="auth-footer" style={{ textAlign: "center" }}>
            <p>
              Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link>
            </p>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default LoginPage;
