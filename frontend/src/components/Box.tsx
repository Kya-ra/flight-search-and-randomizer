import type { ReactNode } from "react";

interface BoxProps {
  children: ReactNode;
}

export default function Box({ children }: BoxProps) {
  return (
    <div
      style={{
        padding: "1rem",
        border: "1px solid #ccc",
        borderRadius: "8px",
        marginBottom: "1rem",
      }}
    >
      {children}
    </div>
  );
}
