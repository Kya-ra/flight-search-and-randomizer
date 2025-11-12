import type { ReactNode } from "react";

interface CardProps {
  title?: string;
  children: ReactNode;w
}

export default function Card({ title, children }: CardProps) {
  return (
    <div
      style={{
        padding: "1rem",
        background: "#242424",
        borderRadius: "10px",
        boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
        marginBottom: "1rem",
      }}
    >
      {title && <h3 style={{ marginTop: 0 }}>{title}</h3>}
      {children}
    </div>
  );
}
